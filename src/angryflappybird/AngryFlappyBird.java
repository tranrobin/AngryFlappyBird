package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

//The Application layer
public class AngryFlappyBird extends Application {

    private Defines DEF = new Defines();

    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;

    // counters
    private int SCORE_COUNTER;
    private int LIVES_COUNTER;

    // game components
    private Sprite koya;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipes;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER, HIT_PIPE;

    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		

    // the mandatory main method 
    public static void main(String[] args) {
        launch(args);
    }

    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {

        // initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
        resetGameScene(true);  // resets the gameScene

        HBox root = new HBox();
        HBox.setMargin(gameScene, new Insets(0,0,0,15));
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

    // the getContent method sets the Scene layer
    private void resetGameControl() {

        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);

        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton);
    }

    private void mouseClickHandler(MouseEvent e) {
        if (GAME_OVER) {
            resetGameScene(false);
        }
        else if (GAME_START){
            clickTime = System.nanoTime();   
        }
        GAME_START = true;
        CLICKED = true;
    }

    // update labels 
    private void updateScoreLabel(int score) {
        DEF.SCORE_LABEL.setText(Integer.toString(score));
    }

    private void updateLivesLabel(int lives) {
        DEF.LIVES_LABEL.setText(Integer.toString(lives) + " lives left");
    }

    //update scores
    private void updateScore() {
        if (!HIT_PIPE) {
            for (int i = 0; i < 4; i++) {
                if (pipes.get(i).getPositionX() + 20 == koya.getPositionX()) {
                    SCORE_COUNTER+=1;
                    updateScoreLabel(SCORE_COUNTER);
                    break;
                }
            }
        }
    }

    private void resetGameScene(boolean firstEntry) {

        // reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        HIT_PIPE = false;
        SCORE_COUNTER = 0;
        LIVES_COUNTER = 3;
        updateScoreLabel(0);
        updateLivesLabel(3);
        floors = new ArrayList<>();
        pipes = new ArrayList<>();

        if(firstEntry) {
            // create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView background = DEF.IMVIEW.get("background");

            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas, 
                    DEF.SCORE_LABEL, DEF.LIVES_LABEL);
        }

        // initialize floor
        for(int i=0; i<DEF.FLOOR_COUNT; i++) {

            int posX = i * DEF.FLOOR_WIDTH;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;

            Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
            floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            floor.render(gc);

            floors.add(floor);
        }

        // initialize koya
        koya = new Sprite(DEF.KOYA_POS_X, DEF.KOYA_POS_Y,DEF.IMAGE.get("koya0"));
        koya.render(gc);

        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();

        // initialize pipes      
        for(int i=0; i<DEF.PIPE_COUNT; i++) {

            int posX = DEF.SCENE_WIDTH + i * DEF.PIPE_GAP;
            int posY = new Random().nextInt(DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1) + DEF.PIPE_MIN_HEIGHT;

            Sprite topPipe = new Sprite(posX, posY, DEF.IMAGE.get("pipe1"));
            topPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            topPipe.render(gc);
            pipes.add(topPipe);

            Sprite bottomPipe = new Sprite(posX, posY + 300 + DEF.PIPE_HEIGHT, DEF.IMAGE.get("pipe0"));
            bottomPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            bottomPipe.render(gc);
            pipes.add(bottomPipe);

        }
    }

    //timer stuff
    class MyTimer extends AnimationTimer {

        int counter = 0;

        @Override
        public void handle(long now) {   		 
            // time keeping
            elapsedTime = now - startTime;
            startTime = now;

            // clear current scene
            gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

            if (GAME_START) {
                // step1: update floor and pipes
                moveFloor();
                movePipe();

                // step2: update koya
                moveKoya();
                checkCollision();
                
                // step3: update score
                updateScore();
            }
        }

        // update floor
        private void moveFloor() {

            for(int i=0; i<DEF.FLOOR_COUNT; i++) {
                if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                    double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
                    double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                    floors.get(i).setPositionXY(nextX, nextY);
                }
                floors.get(i).render(gc);
                floors.get(i).update(DEF.SCENE_SHIFT_TIME);
            }
        }

        // update pipe
        private void movePipe() {
            for(int i = 0; i < DEF.PIPE_COUNT; i++) {

                Sprite topPipe = pipes.get(i * 2);
                Sprite bottomPipe = pipes.get(i * 2 + 1);

                if (topPipe.getPositionX() <= -DEF.PIPE_WIDTH) {
                    double nextX = pipes.get((i+1) % DEF.PIPE_COUNT * 2).getPositionX() + 300;
                    double nextY = new Random().nextInt(DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1) + DEF.PIPE_MIN_HEIGHT;
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


        private void moveKoya() {

            long diffTime = System.nanoTime() - clickTime;

            // koya flies upward with animation
            if (CLICKED && diffTime <= DEF.KOYA_DROP_TIME) {

                int imageIndex = Math.floorDiv(counter++, DEF.KOYA_IMG_PERIOD);
                imageIndex = Math.floorMod(imageIndex, DEF.KOYA_IMG_LEN);
                koya.setImage(DEF.IMAGE.get("koya"+String.valueOf(imageIndex)));
                koya.setVelocity(0, DEF.KOYA_FLY_VEL);
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

        public void checkCollision() {
            // check floor collision  
            for (Sprite floor: floors) {
                GAME_OVER = GAME_OVER || koya.intersectsSprite(floor);
            }


            // check pipe collision
            for (Sprite pipe : pipes) {
                HIT_PIPE = HIT_PIPE || koya.intersectsSprite(pipe);
            }

            if (HIT_PIPE) { // not working correctly
                if (LIVES_COUNTER > 0) {
                    LIVES_COUNTER -= 1;
                    System.out.print(LIVES_COUNTER);
                    updateLivesLabel(LIVES_COUNTER);
                } 
                else {
                    GAME_OVER = true;
                }
            }

            // end the game when koya hit floors or hit pipes more than 3 times
            if (GAME_OVER) {
                showHitEffect(); 
                for (Sprite floor: floors) {
                    floor.setVelocity(0, 0);
                }
                for(Sprite pipe: pipes) {
                    pipe.setVelocity(0, 0);
                }
                timer.stop();
            }
        }



        private void showHitEffect() {
            ParallelTransition parallelTransition = new ParallelTransition();
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
            fadeTransition.setToValue(0);
            fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
            fadeTransition.setAutoReverse(true);
            parallelTransition.getChildren().add(fadeTransition);
            parallelTransition.play();
        }

    } // End of MyTimer class

} // End of AngryFlappyBird Class

