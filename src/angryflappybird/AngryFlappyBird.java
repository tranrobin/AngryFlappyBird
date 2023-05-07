package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of Angry Flappy Bird game 
 * The application layer
 * @author Robin Tran, Jennifer Pham
 *
 */
public class AngryFlappyBird extends Application {

    private Defines DEF = new Defines();

    // time related attributes
    private long clickTime, startTime, elapsedTime, backgroundShiftTime,
    hitTime;
    private AnimationTimer timer;

    // counters
    private int SCORE_COUNTER;
    private int LIVES_COUNTER;

    // game components
    private Sprite koya;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipes;
    private ArrayList<Sprite> avocados;
    private ArrayList<Sprite> carrots;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    private boolean HIT_PIPE, HIT_CARROT, GET_AVOCADO, GET_GOLDEN,
    CARROT_GET_AVOCADO, CARROT_GET_GOLDEN;

    // scene graphs
    private Group gameScene; // the left half of the scene
    private VBox gameControl; // the right half of the GUI (control)
    ChoiceBox<String> difficultyMenu = new ChoiceBox<>(); // the difficulty menu implemented as a choice box
    private GraphicsContext gc;

    private ImageView background; // game background

    // the mandatory main method
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The start method sets the Stage layer
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // initialize scene graphs and UIs
        resetGameControl(); // resets the gameControl
        resetGameScene(true); // resets the gameScene

        HBox root = new HBox();
        HBox.setMargin(gameScene, new Insets(0, 0, 0, 15));
        root.getChildren().add(gameScene);
        root.getChildren().add(gameControl);

        // add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);

        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * The getContent method sets the Scene layer
     */
    private void resetGameControl() {

        // set a mouse click handler for the start button
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);

        // create a drop-down menu for difficulty and add options
        difficultyMenu.getItems().addAll("Easy", "Medium", "Difficult");
        difficultyMenu.setValue("Easy");

        // load the images for the different items in the game description
        ImageView avocadoImage = DEF.IMVIEW.get("avocado");
        ImageView goldenImage = DEF.IMVIEW.get("yellowavocado");
        ImageView carrotImage = DEF.IMVIEW.get("carrot");

        // set size for all images in the game description
        avocadoImage.setFitWidth(70);
        avocadoImage.setFitHeight(70);
        goldenImage.setFitWidth(70);
        goldenImage.setFitHeight(70);
        carrotImage.setFitWidth(70);
        carrotImage.setFitHeight(70);

        // create a horizontal box and a text description for each game item
        HBox avocadoDes = new HBox(); // green avocado description
        Text avocado = new Text("Add 5 points");
        avocadoDes.setAlignment(Pos.CENTER_LEFT);
        avocadoDes.getChildren().addAll(avocadoImage, avocado);

        HBox goldenDes = new HBox(); // golden avocado description
        Text golden = new Text("Let you snooze");
        goldenDes.setAlignment(Pos.CENTER_LEFT);
        goldenDes.getChildren().addAll(goldenImage, golden);

        HBox carrotDes = new HBox(); // carrot description
        Text carrot = new Text("Avoid the carrots");
        carrotDes.setAlignment(Pos.CENTER_LEFT);
        carrotDes.getChildren().addAll(carrotImage, carrot);

        // create a vertical box to hold all the game control elements
        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton, difficultyMenu,
                avocadoDes, goldenDes, carrotDes);

        // position the start button and difficulty menu
        DEF.startButton.setTranslateX(10);
        DEF.startButton.setTranslateY(10);
        difficultyMenu.setTranslateX(10);
        difficultyMenu.setTranslateY(30);

        // position the items description
        avocadoDes.setTranslateX(0);
        avocadoDes.setTranslateY(100);
        goldenDes.setTranslateX(0);
        goldenDes.setTranslateY(120);
        carrotDes.setTranslateX(3);
        carrotDes.setTranslateY(140);
    }

    /**
     * Method to handle mouse click events
     * @param e
     */
     
    private void mouseClickHandler(MouseEvent e) {

        // check if the game is over and reset the game scene if it is
        if (GAME_OVER) {
            resetGameScene(false);

            // if the game has started, record the time of the click
        } else if (GAME_START) {
            clickTime = System.nanoTime();
        }
        GAME_START = true;
        CLICKED = true;
    }

    /**
     * Update the score label on the game scene
     * @param score
     */
    private void updateScoreLabel(int score) {
        DEF.SCORE_LABEL.setText(Integer.toString(score));
    }

    /**
     * Update the lives label on the game scene
     * @param lives
     */
    private void updateLivesLabel(int lives) {
        DEF.LIVES_LABEL.setText(Integer.toString(lives) + " lives left");
    }
    
//    private void updateTimerLabel(int time) {
//        DEF.TIMER_LABEL.setText(Integer.toString(time) + " secs to go");
//    }

    /**
     * Method to update the score after different events, such as when the Koya passes 
     * pipes, the Koya collects green avocados, or the carrot gets the avocados
     */
    private void updateScore() {
        if (!HIT_PIPE) {
            // update the score if the Koya passes 1 pipe
            for (int i = 0; i < 4; i++) {
                if (pipes.get(i).getPositionX() + 10 == koya.getPositionX()) { 
                    DEF.AUDIO.get("point").play();
                    SCORE_COUNTER += 1;
                    break;
                }
            }
            // update the score if the Koya gets a green avocado
            if (GET_AVOCADO) {
                SCORE_COUNTER += 5;
                DEF.AUDIO.get("point").play();
                avocados.get(0).setPositionXY(pipes.get(2).getPositionX(),
                        1000);
                GET_AVOCADO = false;

            }
            // update the score if the carrot steals an avocado
            if (CARROT_GET_AVOCADO || CARROT_GET_GOLDEN) {
                SCORE_COUNTER -= 5;
                avocados.get(0).setPositionXY(pipes.get(2).getPositionX(),
                        1000);
                CARROT_GET_AVOCADO = false;
                CARROT_GET_GOLDEN = false;
            }
        }
        // update the score on the game scene
        updateScoreLabel(SCORE_COUNTER);
    }

    /**
     * Resets the game scene and initializes necessary variables, lists, and objects
     * based on whether it is the first entry or not
     *  @param firstEntry a boolean value indicating whether it is the first entry to the game scene
     */
    private void resetGameScene(boolean firstEntry) {

        // reset variables after the game is over
        if (GAME_OVER) {
            DEF.GameOver.setText("");
            DEF.GetReady.setText("GET READY");
            SCORE_COUNTER = 0;
            LIVES_COUNTER = 3;
            updateScoreLabel(0);
            updateLivesLabel(3);
        }

        // reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        HIT_PIPE = false;
        HIT_CARROT = false;
        GET_AVOCADO = false;
        GET_GOLDEN = false;
        CARROT_GET_AVOCADO = false;
        CARROT_GET_GOLDEN = false;
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        avocados = new ArrayList<>();
        carrots = new ArrayList<>();
        DEF.GetReady.setText("GET READY");

        if (firstEntry) {

            // reset the score and lives counter to their initial values
            SCORE_COUNTER = 0;
            LIVES_COUNTER = 3;

            // update the score and lives label with the initial values
            updateScoreLabel(0);
            updateLivesLabel(3);
            
            // create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            background = DEF.IMVIEW.get("background");

            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas, DEF.SCORE_LABEL,
                    DEF.LIVES_LABEL, DEF.TIMER_LABEL, DEF.GetReady,
                    DEF.GameOver);

            gameScene.setOnMouseClicked(this::mouseClickHandler);
        }

        // initialize floor
        for (int i = 0; i < DEF.FLOOR_COUNT; i++) {

            int posX = i * DEF.FLOOR_WIDTH;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;

            Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
            floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            floor.render(gc);

            floors.add(floor);
        }

        // initialize koya
        koya = new Sprite(DEF.KOYA_POS_X, DEF.KOYA_POS_Y,
                DEF.IMAGE.get("koya0"));
        koya.render(gc);

        // initialize timer
        startTime = System.nanoTime();
        backgroundShiftTime = DEF.BACKGROUND_SHIFT_TIME;
        timer = new MyTimer();
        timer.start();
        hitTime = 0;

        // initialize pipes
        int posX = 0;
        int posY = 0;
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {

            posX = DEF.SCENE_WIDTH + i * DEF.PIPE_GAP;
            posY = new Random()
                    .nextInt(DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1)
                    + DEF.PIPE_MIN_HEIGHT;

            Sprite topPipe = new Sprite(posX, posY, DEF.IMAGE.get("pipe1"));
            topPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            topPipe.render(gc);
            pipes.add(topPipe);

            Sprite bottomPipe = new Sprite(posX, posY + 300 + DEF.PIPE_HEIGHT,
                    DEF.IMAGE.get("pipe0"));
            bottomPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            bottomPipe.render(gc);
            pipes.add(bottomPipe);

        }
        
        // initialize avocados
        Sprite avocado = new Sprite(posX - 300,
                pipes.get(1).getPositionY() - DEF.AVOCADO_HEIGHT,
                DEF.IMAGE.get("avocado"));
        avocado.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        avocado.render(gc);
        avocados.add(avocado);

        Sprite golden = new Sprite(posX,
                pipes.get(3).getPositionY() - DEF.AVOCADO_HEIGHT,
                DEF.IMAGE.get("yellowavocado"));
        golden.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        golden.render(gc);
        avocados.add(golden);

        // initialize carrot
        Sprite carrot = new Sprite(posX, posY - 100, DEF.IMAGE.get("carrot"));
        carrot.setVelocity(DEF.SCENE_SHIFT_INCR, 0.2);
        carrot.render(gc);
        carrots.add(carrot);
        carrots.add(carrot);
    }

    /**
     * This class is used to handle the timing of game events and updates. 
     * It keeps track of the elapsed time since the start of the game and clears the current scene at 
     * each frame
     * @author Robin Tran, Jennifer Pham
     */
    class MyTimer extends AnimationTimer {

        // initialize counter
        int counter = 0;

        @Override
        /**
         * Handles the game animation by updating the elapsed time, clearing the current scene, 
         * and executing the game logic while the game is in progress
         * @param now the current time
         */
        public void handle(long now) {
            // time keeping
            elapsedTime = now - startTime;
            startTime = now;

            // clear current scene
            gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

            if (GAME_START) {

                if (pipes.get(0).getPositionX() == DEF.SCENE_WIDTH - 50) {
                    DEF.GetReady.setText("");
                }

                // step1: update floor and pipes
                moveFloor();
                movePipe();

                // step2 update avocados and carrots
                moveAvocado();
                moveCarrot();

                // step3: update koya
                moveKoya();
                
                // step4: check collision
                if (!GET_GOLDEN) {
                    checkCollision();
                }
                
                // step5: update score and change background
                updateScore();
                changeBackground();
            }
        }

        /**
         * Update the floor throughout the game
         */
        private void moveFloor() {

            for (int i = 0; i < DEF.FLOOR_COUNT; i++) {
                if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                    double nextX = floors.get((i + 1) % DEF.FLOOR_COUNT)
                            .getPositionX() + DEF.FLOOR_WIDTH;
                    double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                    floors.get(i).setPositionXY(nextX, nextY);
                }
                floors.get(i).render(gc);
                floors.get(i).update(DEF.SCENE_SHIFT_TIME);
            }
        }

        /**
         * Update the pipes throughout the game
         */
        private void movePipe() {
            for (int i = 0; i < DEF.PIPE_COUNT; i++) {

                Sprite topPipe = pipes.get(i * 2);
                Sprite bottomPipe = pipes.get(i * 2 + 1);

                // Position the pipes
                if (topPipe.getPositionX() <= -DEF.PIPE_WIDTH) {
                    double nextX = pipes.get((i + 1) % DEF.PIPE_COUNT * 2)
                            .getPositionX() + 300;
                    double nextY = new Random().nextInt(
                            DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1)
                            + DEF.PIPE_MIN_HEIGHT;
                    topPipe.setPositionXY(nextX, nextY);
                    bottomPipe.setPositionXY(nextX, nextY + 500);
                }

                topPipe.update(DEF.SCENE_SHIFT_TIME);
                bottomPipe.update(DEF.SCENE_SHIFT_TIME);
                topPipe.render(gc);
                bottomPipe.render(gc);
                updateScoreLabel(SCORE_COUNTER);
            }
        }

        /**
         * Update the koya throughout the game, based on different events
         */
        private void moveKoya() {

            long diffTime = System.nanoTime() - clickTime;

            long now = System.nanoTime();
            float seconds = (now - hitTime) / 1000000000;

            boolean played = false;

            // koya gets into autopilot
            if (GET_GOLDEN && seconds <= 6 && !HIT_CARROT && !HIT_PIPE) {

                if (seconds == 0 && !played) {
                    played = true;
                    DEF.AUDIO.get("snooze").play();
                }

                int secondsLeft = 6 - (int) seconds;
                DEF.TIMER_LABEL.setText(Integer.toString(secondsLeft) + " secs to go");
                koya.setImage(DEF.IMAGE.get("koya")); // change the picture of koya
                koya.setVelocity(0, -10);

                if (seconds == 6) { // snooze mode ends
                    GET_GOLDEN = false;
                    DEF.TIMER_LABEL.setText("");
                }
            }

            // koya flies upward with animation
            else if (CLICKED && diffTime <= DEF.KOYA_DROP_TIME && !HIT_CARROT
                    && !HIT_PIPE) {
                int imageIndex = Math.floorDiv(counter++, DEF.KOYA_IMG_PERIOD);
                imageIndex = Math.floorMod(imageIndex, DEF.KOYA_IMG_LEN);
                koya.setImage(
                        DEF.IMAGE.get("koya" + String.valueOf(imageIndex)));
                koya.setVelocity(0, DEF.KOYA_FLY_VEL);
            }

            // koya bounces back when collides with pipes or carrots
            else if (HIT_PIPE || HIT_CARROT) {
                DEF.AUDIO.get("hit").play();
                koya.setVelocity(-500, 500);
            }
            
            // koya drops after a period of time without button click
            else {
                koya.setVelocity(0, DEF.KOYA_DROP_VEL);
                CLICKED = false;
            }

            // render koya on GUI
            koya.update(elapsedTime * DEF.NANOSEC_TO_SEC);
            koya.render(gc);
        }
        
        /**
         * Update the avocados throughout the game
         */
        private void moveAvocado() {
            Sprite avocado = avocados.get(0);
            Sprite golden = avocados.get(1);

            if (avocado.getPositionX() <= -DEF.AVOCADO_WIDTH
                    && golden.getPositionX() <= -DEF.AVOCADO_WIDTH) {

                // randomly assign avocados on bottom pipes
                int pipeIndex = (int) (Math.random() * 2) + 2;
                double nextX = pipes.get(pipeIndex).getPositionX();
                double nextY = pipes.get(pipeIndex).getPositionY()
                        - DEF.AVOCADO_HEIGHT;

                // randomly choose green or golden avocados to put on pipes
                int avocadoIndex = (int) Math.round(Math.random());
                if (avocadoIndex == 0)
                    avocado.setPositionXY(nextX, nextY);
                else if (avocadoIndex == 1)
                    golden.setPositionXY(nextX, nextY);
            }
            
            avocado.update(DEF.SCENE_SHIFT_TIME);
            avocado.render(gc);
            golden.update(DEF.SCENE_SHIFT_TIME);
            golden.render(gc);

            // update the variables if koya gets the avocados
            GET_AVOCADO = GET_AVOCADO || (HIT_PIPE == false
                    && HIT_CARROT == false && koya.intersectsSprite(avocado));

            if (koya.intersectsSprite(golden) && HIT_PIPE == false
                    && HIT_CARROT == false) {
                GET_GOLDEN = true;
                avocados.get(1).setPositionXY(pipes.get(2).getPositionX(),
                        1000);
                hitTime = System.nanoTime();
            }
        }

        /**
         * Update the carrots throughout the game
         */
        private void moveCarrot() {

            Sprite carrot = carrots.get(0);

            // get the difficulty level
            String difficulty = difficultyMenu.getValue();

            // randomly assigns carrots to drop down from upper pipes
            if (carrot.getPositionX() <= -DEF.CARROT_WIDTH) {
                double random = (Math.random());
                double nextX = 0;
                double nextY = 0;

                // determine the probability of the carrots based on 
                // the difficulty level
                if (difficulty.equals("Easy")) {
                    if (random > 0.7) {
                        nextX = pipes.get(2).getPositionX();
                        nextY = pipes.get(2).getPositionY() - 100;
                        carrot.setPositionXY(nextX, nextY);
                    } else if (random <= 0.7) {
                        nextX = pipes.get(2).getPositionX();
                        nextY = 1000;
                        carrot.setPositionXY(nextX, nextY);
                    }
                } else if (difficulty.equals("Medium")) {
                    if (random > 0.3) {
                        nextX = pipes.get(2).getPositionX();
                        nextY = pipes.get(2).getPositionY();
                        carrot.setPositionXY(nextX, nextY);
                    } else if (random <= 0.3) {
                        nextX = pipes.get(2).getPositionX();
                        nextY = 1000;
                        carrot.setPositionXY(nextX, nextY);
                    }
                } else if (difficulty.equals("Difficult")) {
                    if (random > 0.1) {
                        nextX = pipes.get(2).getPositionX();
                        nextY = 0;
                        carrot.setPositionXY(nextX, nextY);
                    } else if (random <= 0.1) {
                        nextX = pipes.get(2).getPositionX();
                        nextY = 1000;
                        carrot.setPositionXY(nextX, nextY);
                    }
                }
            }
            
            carrot.update(DEF.SCENE_SHIFT_TIME);
            carrot.render(gc);
        }

        /**
         * Checks if the Koya collides with any pipes in the game,
         * and update lives and scores accordingly
         */
        private void checkCollisionWithPipe() {
            // check pipe collision
            if (koya.intersectsSprite(pipes.get(0))
                    || koya.intersectsSprite(pipes.get(1))
                    || koya.intersectsSprite(pipes.get(2))
                    || koya.intersectsSprite(pipes.get(3))) {

                HIT_PIPE = true;

                for (Sprite pipe : pipes) {
                    pipe.setVelocity(0, 0);
                }

                for (Sprite floor : floors) {
                    floor.setVelocity(0, 0);
                }

                for (Sprite carrot : carrots) {
                    carrot.setVelocity(0, 0);
                }

                for (Sprite avocado : avocados) {
                    avocado.setVelocity(0, 0);
                }
            }
            
            // update lives
            if (HIT_PIPE && koya.getPositionX() < -DEF.KOYA_WIDTH) {
                LIVES_COUNTER--;
                updateLivesLabel(LIVES_COUNTER);
                GAME_OVER = GAME_OVER || LIVES_COUNTER == 0; // game over if there is no live left
                if (!GAME_OVER) {
                    timer.stop();
                    resetGameScene(false);
                }
            }
        }

        /**
         * Checks if the Koya collides with any carrots in the game,
         * and update GAME OVER accordingly
         */
        private void checkCollisionWithCarrot() {
            // check carrot collision
            if (koya.intersectsSprite(carrots.get(0))) {

                HIT_CARROT = true;

                for (Sprite pipe : pipes) {
                    pipe.setVelocity(0, 0);
                }

                for (Sprite floor : floors) {
                    floor.setVelocity(0, 0);
                }

                for (Sprite carrot : carrots) {
                    carrot.setVelocity(0, 0);
                }

                for (Sprite avocado : avocados) {
                    avocado.setVelocity(0, 0);
                }
            }

            // if koya hits carrot, game is over
            if (HIT_CARROT && koya.getPositionX() < -DEF.KOYA_WIDTH) {
                GAME_OVER = true;
                updateLivesLabel(0);
            }
        }

        /**
         * Checks if the Koya collides with any objects in the game,
         * and update lives and scores accordingly
         */
        public void checkCollision() {

            // check floor collision
            if (!HIT_PIPE && !HIT_CARROT) {
                for (Sprite floor : floors) {
                    GAME_OVER = GAME_OVER || koya.intersectsSprite(floor);
                }
            }

            checkCollisionWithCarrot();

            checkCollisionWithPipe();

            // end the game when koya hit floors or hit pipes more than 3 times
            if (GAME_OVER) {

                // update the audio, sign, and lives label
                DEF.AUDIO.get("die").play();

                DEF.GameOver.setText("GAME OVER");

                if (LIVES_COUNTER == 0) {
                    updateLivesLabel(LIVES_COUNTER);
                }

                showHitEffect();

                // stop the game
                for (Sprite floor : floors) {
                    floor.setVelocity(0, 0);
                }
                for (Sprite pipe : pipes) {
                    pipe.setVelocity(0, 0);
                }
                timer.stop();
            }
        }

        /**
         * Changes the background image of the game based on the elapsed time and predefined background shift time.
         * If the elapsed time exceeds the background shift time, the background image is switched between day and night
         */
        private void changeBackground() {
            long now = System.nanoTime();
            float time = (now - backgroundShiftTime) / 1000000000;
            if (time > DEF.BACKGROUND_SHIFT_TIME) {
                if (background.getImage() == DEF.IMAGE.get("background")) {
                    background.setImage(DEF.IMAGE.get("background-night"));
                } else if (background.getImage() == DEF.IMAGE
                        .get("background-night")) {
                    background.setImage(DEF.IMAGE.get("background"));
                }
                backgroundShiftTime = System.nanoTime();
            }
        }

        /**
         * Implement hit effect used when collision happens
         */
        private void showHitEffect() {
            ParallelTransition parallelTransition = new ParallelTransition();
            FadeTransition fadeTransition = new FadeTransition(
                    Duration.seconds(DEF.TRANSITION_TIME), gameScene);
            fadeTransition.setToValue(0);
            fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
            fadeTransition.setAutoReverse(true);
            parallelTransition.getChildren().add(fadeTransition);
            parallelTransition.play();

        }

    } // End of MyTimer class

} // End of AngryFlappyBird Class
