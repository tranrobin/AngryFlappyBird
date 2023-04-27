package angryflappybird;

import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Defines {
    

    // dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to score display

    final int SCORE_POS_X = 10;

    final int SCORE_POS_Y = 30;

    // coefficients related to lives count display

    final int LIVES_POS_X = 280;

    final int LIVES_POS_Y = 530;

    // coefficients related to the blob
    final int BLOB_WIDTH = 80;
    final int BLOB_HEIGHT = 80;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 300;    		// the blob drop velocity
    final int BLOB_FLY_VEL = -40;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;

    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 80;
    final int FLOOR_COUNT = 2;

    // coefficients related to the pipes
    final int PIPE_WIDTH = 100;
    final int PIPE_HEIGHT = 210;
    final int PIPE_GAP = 300;
    final int PIPE_COUNT = 2;
    final int PIPE_MIN_HEIGHT = -120;
    final int PIPE_MAX_HEIGHT = 0;
    final int PIPE_POS_X = 500;
    
    final int AVOCADO_WIDTH = 100;
    final int AVOCADO_HEIGHT = 100;
    final int AVOCADO_COUNT = 2;

    final int CARROT_WIDTH = 100;
    final int CARROT_HEIGHT = 100;

    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
    private final String IMAGE_DIR = "../resources/images/";
    final String[] IMAGE_FILES = {"background","koya0", "koya1", "koya2", "koya3", "floor", "pipes-3", "pipes-4", "avocado", "yellowavocado", "carrot"};

    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();

    //nodes on the scene graph
    Button startButton;
    Button easyLevelButton;
    Button mediumLevelButton;
    Button hardLevelButton;

    // constructor
    Defines() {

        // initialize images
        for(int i=0; i<IMAGE_FILES.length; i++) {
            Image img;
            if (i == 5) {
                img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
            }
            else if (i == 1 || i == 2 || i == 3 || i == 4){
                img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
            }
            else if ( i == 6 || i == 7) {
                img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE_HEIGHT, false, false);
            }
            else if ( i == 8) {
                img = new Image(pathImage(IMAGE_FILES[i]), AVOCADO_WIDTH, AVOCADO_HEIGHT, false, false);
            }
            else if ( i == 9) {
                img = new Image(pathImage(IMAGE_FILES[i]), AVOCADO_WIDTH, AVOCADO_HEIGHT, false, false);
            }
            else if (i == 10) {
                img = new Image(pathImage(IMAGE_FILES[i]), CARROT_WIDTH, CARROT_HEIGHT, false, false);
            }
            else {
                img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
            }
            IMAGE.put(IMAGE_FILES[i],img);
        }

        // initialize image views
        for(int i=0; i<IMAGE_FILES.length; i++) {
            ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
            IMVIEW.put(IMAGE_FILES[i],imgView);
        }

        // initialize scene nodes
        startButton = new Button("Go!");
        easyLevelButton = new Button("Easy");
        mediumLevelButton = new Button("Medium");
        hardLevelButton = new Button("Hard");
    }

    public String pathImage(String filepath) {
        String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
        return fullpath;
    }

    public Image resizeImage(String filepath, int width, int height) {
        IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
        return IMAGE.get(filepath);
    }
}
