import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.lang.Character;

public class Dungeon extends Application
{
    
    public static Stage stage;
    private Scene scene;
    private Parent root;

    // Images in the game.
    public static Image back;
    public static Image iconImage;
    public static Image zeldaImage;
    public static Image backgroundImage;
    public static Image zeldaSpriteImage;
    public static Image levelComplete;
    public static Image button;
    public static Image box1;
    public static Image box2;
    public static Image box3;
    public static Image box4;
    public static Image reel;
    public static Image[] mapImage;

    // Images (.gif)
    public static Image coin;
    public static Image thronOut;
    public static Image thronIn;

    // Musics in the game.
    public static MediaPlayer beginPlayer;

    //  Map's Infomation 
    public static int[][][] mapInfo;
    public static int[] levelWidth;
    public static int[] levelHeight;
    public static int[] levelStrength;

    // Level's information (Completed or not)
    public static int levelStatus[] = new int[]{ 
       -1,
        1, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0 
    };

    // Main function
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception  {

        root = FXMLLoader.load(getClass().getResource("begin.fxml"));

        loadImages();
        loadMusics();
        loadMap();
        readLevelInfo();
        
        initializeStage();

        scene = new Scene(root, 1152, 648);
        stage.setScene(scene);
        stage.show();
    }

    private void loadImages() {

        levelComplete = new Image("Images\\levelComplete.png");
        back = new Image("Images\\back.png");
        iconImage = new Image("Images\\iconImage.png");
        zeldaImage= new Image("Images\\zeldaImage.jpg");
        backgroundImage = new Image("Images\\game.jpg");
        zeldaSpriteImage = new Image("Images\\zeldaSprite.png");
        box1 = new Image("Images\\box1.png");
        box2 = new Image("Images\\box2.png");
        box3 = new Image("Images\\box3.png");
        box4 = new Image("Images\\box4.png");
        reel = new Image("Images\\reel.png");

        coin = new Image(this.getClass().getResource("Images\\coin.gif").toExternalForm());
        thronIn = new Image(this.getClass().getResource("Images\\thronIn.gif").toExternalForm());
        thronOut = new Image(this.getClass().getResource("Images\\thronOut.gif").toExternalForm());

        mapImage = new Image[30];

        for(int i=0 ; i<=10 ; i++) {
            String num = Integer.toString(i);
            mapImage[i] = new Image("Images\\"+num+".png");
        }
    }

    private void loadMusics() {
        Media beginMusic = new Media(new File("src\\Songs\\beginMusic.mp3").toURI().toString());
        beginPlayer = new MediaPlayer(beginMusic);
        beginPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        beginPlayer.play();
    }

    private void loadMap() throws IOException {

        // mapInfo[level][height][width]
        FileReader fr = new FileReader("src\\mapInfomation.txt");
        BufferedReader br = new BufferedReader(fr);

        mapInfo = new int[25][15][15];
        levelWidth = new int[25];
        levelHeight = new int[25];
        levelStrength = new int[25];

        int level = 0;
        int width = 0;
        int height = 0;
        int strength = 0;

        boolean start = false;
        boolean canReadInfo = false;

        String str = null;
        while( (str = br.readLine()) != null) {

            // When read the text "Level", it means that I can start reading the infomation.
            if(str.equals("Level")) {
                start = true;
                continue;
            }
            
            // Read the level, height and width
            if(start && !canReadInfo) {
                String[] tmp = str.split(" ");
                level = Integer.parseInt(tmp[0]);
                height =  Integer.parseInt(tmp[1]);
                width = Integer.parseInt(tmp[2]);
                strength = Integer.parseInt(tmp[3]);

                levelHeight[level] = height;
                levelWidth[level] = width;               
                levelStrength[level] = strength;

                canReadInfo = true;
                continue;
            }

            // Read the map
            if(start && canReadInfo) {
                for(int i=0 ; i<height ; i++) {
                    str = br.readLine().trim();
                    String[] tmp = str.split(",");
                    
                    for(int j=0 ; j<width ; j++) {
                        mapInfo[level][i][j] = Integer.parseInt(tmp[j]);
                    }
                }
                // str = br.readLine();        //read }

                start = false;
                canReadInfo = false;
            }
        }

        br.close();
        fr.close();
    }

    private void initializeStage() {

        // initialize the stage
        stage = new Stage();
        stage.setTitle("Escape The Dungeon");
        stage.getIcons().add(iconImage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setWidth(1152);
        stage.setHeight(648);
    }



    public static void readLevelInfo() throws Exception {
        FileReader fr = new FileReader("src\\LevelInfo.txt");

        char[] info = new char[150];
        int tmp = fr.read(info);
        
        for(int i=0, level=1; i<tmp ; i++) {

            if(Character.isDigit(info[i])) {
                levelStatus[level++] = Character.getNumericValue(info[i]);
            }
            else if(info[i] == '<') break;
        }

        fr.close();
    }

    public static void writeLevelInfo(boolean isNewGame) throws Exception
    {
        FileWriter fw = new FileWriter("src\\LevelInfo.txt", false);

        if(isNewGame == true) {
            for(int i=1 ; i<=24 ; i++) {
                if(i == 1) fw.write(">>> 1, ");
                else if(i == 24) fw.write("0<<<");
                else fw.write("0, ");
            }
        }
        else {
            for(int i=1 ; i<=24 ; i++) {
                if(i == 1) fw.write((levelStatus[i] == 1) ? ">>> 1, " : ">>> 0, ");
                else if(i == 24) fw.write((levelStatus[i] == 1) ? "1<<<" : "0<<<");
                else fw.write((levelStatus[i] == 1) ? "1, " : "0, ");
            }
        }

        fw.close();
    } 
}
