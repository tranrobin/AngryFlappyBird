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

    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipes;
    private ArrayList<Sprite> avocados;
    private ArrayList<Sprite> carrots;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER, PIPE_COLLISION;

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
        VBox difficultyMenu = new VBox(DEF.easyLevelButton, DEF.mediumLevelButton, DEF.hardLevelButton);
        DEF.easyLevelButton.setPrefWidth(100);
        DEF.mediumLevelButton.setPrefWidth(100);
        DEF.hardLevelButton.setPrefWidth(100);
        DEF.easyLevelButton.setAlignment(Pos.BASELINE_LEFT);
        DEF.mediumLevelButton.setAlignment(Pos.BASELINE_LEFT);
        DEF.hardLevelButton.setAlignment(Pos.BASELINE_LEFT);

        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton);
        gameControl.getChildren().addAll(difficultyMenu);
        DEF.startButton.setTranslateX(10);
        DEF.startButton.setTranslateY(10);
        difficultyMenu.setTranslateX(10);
        difficultyMenu.setTranslateY(30);
       
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

    private void resetGameScene(boolean firstEntry) {

        // reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        PIPE_COLLISION = false;
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
            gameScene.getChildren().addAll(background, canvas);
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

        // initialize pipes
        int posX = 0;
        int posY = 0;

        for(int i=0; i<DEF.PIPE_COUNT; i++) {

            posX = DEF.SCENE_WIDTH + i * DEF.PIPE_GAP;
            posY = new Random().nextInt(DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1) + DEF.PIPE_MIN_HEIGHT;

            Sprite topPipe = new Sprite(posX, posY, DEF.IMAGE.get("pipes-4"));
            topPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            topPipe.render(gc);
            pipes.add(topPipe);

            Sprite bottomPipe = new Sprite(posX, posY + 300 + DEF.PIPE_HEIGHT, DEF.IMAGE.get("pipes-3"));
            bottomPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            bottomPipe.render(gc);
            pipes.add(bottomPipe);
        }

        // initialize avocados
        avocados = new ArrayList<>();

        Sprite avocado = new Sprite(posX - 300, pipes.get(1).getPositionY() - DEF.AVOCADO_HEIGHT, DEF.IMAGE.get("avocado"));
        avocado.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        avocado.render(gc);
        avocados.add(avocado);

        Sprite golden = new Sprite(posX, 1000, DEF.IMAGE.get("yellowavocado"));
        golden.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        golden.render(gc);
        avocados.add(golden);

        // initialize carrots

        carrots = new ArrayList<>();

        Sprite carrot = new Sprite(posX, -100, DEF.IMAGE.get("carrot"));
        carrot.setVelocity(DEF.SCENE_SHIFT_INCR, 0.2); 
        carrot.render(gc);
        carrots.add(carrot);


        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("koya0"));
        blob.render(gc);

        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
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
                // step1: update floor
                moveFloor();
                // step2: update blob
                movePipes();
                moveAvocado();
                moveCarrot();
                moveBlob();

                checkCollision();
            }
        }

        // step1: update floor
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

        // step2: update blob
        private void moveBlob() {

            long diffTime = System.nanoTime() - clickTime;

            // blob flies upward with animation

            if (PIPE_COLLISION) {
                blob.setVelocity(0, -100); 
            }

            if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {

                int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
                imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
                blob.setImage(DEF.IMAGE.get("koya"+String.valueOf(imageIndex)));
                blob.setVelocity(0, DEF.BLOB_FLY_VEL);
            }
            // blob drops after a period of time without button click
            else {
                blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
                CLICKED = false;
            }

            // render blob on GUI
            blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
            blob.render(gc);
        }

        private void movePipes() {

            double nextX = 0;
            double nextY = 0;

            for(int i = 0; i < DEF.PIPE_COUNT; i++) {

                Sprite topPipe = pipes.get(i * 2);
                Sprite bottomPipe = pipes.get(i * 2 + 1);

                if (topPipe.getPositionX() <= -DEF.PIPE_WIDTH) {

                    nextX = pipes.get((i+1) % DEF.PIPE_COUNT * 2).getPositionX() + 300;
                    nextY = new Random().nextInt(DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1) + DEF.PIPE_MIN_HEIGHT;

                    topPipe.setPositionXY(nextX, nextY);
                    bottomPipe.setPositionXY(nextX, nextY + 500);
                } 

                topPipe.update(DEF.SCENE_SHIFT_TIME);
                bottomPipe.update(DEF.SCENE_SHIFT_TIME);
                topPipe.render(gc);
                bottomPipe.render(gc);
            }
        }
    }

    private void moveAvocado() {

        Sprite avocado = avocados.get(0);
        Sprite golden = avocados.get(1);

        if (avocado.getPositionX() <= - DEF.AVOCADO_WIDTH && golden.getPositionX() <= - DEF.AVOCADO_WIDTH) {

            int pipeIndex = (int) (Math.random() * 2) + 2;

            double nextX = pipes.get(pipeIndex).getPositionX();
            double nextY = pipes.get(pipeIndex).getPositionY() - DEF.AVOCADO_HEIGHT;

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

    }

    private void moveCarrot() {

        Sprite carrot = carrots.get(0);
        
        if (carrot.getPositionX() <= - DEF.CARROT_WIDTH) {

            double random = (Math.random());
            
            if (random > 0.6) {
                double nextX = pipes.get(2).getPositionX();
                double nextY = -100;
                carrot.setPositionXY(nextX, nextY);
            }
            else if (random <= 0.6) {
                double nextX = pipes.get(2).getPositionX();
                double nextY = 1000;
                carrot.setPositionXY(nextX, nextY);
            }
        }
        
        carrot.update(DEF.SCENE_SHIFT_TIME);
        carrot.render(gc);
    }

    public void checkCollision() {
        // check collision  
        for (Sprite floor: floors) {
            GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
        }

        for (Sprite pipe: pipes) {
            GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
        }

        // end the game when blob hit stuff
        if (GAME_OVER) {
            showHitEffect(); 
            for (Sprite floor: floors) {
                floor.setVelocity(0, 0);
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


}
// End of AngryFlappyBird Class
