import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameLevel {

    private Group root = new Group();
    public Scene scene;

    private Label completeLabel, failLabel, stepLabel1, stepLabel2, levelLabel, spotLabel1, spotLabel2, skip;
    private Button backButton;
    private ImageView spot;
    private ImageView blackView;

    Character potato;
    Chunk[][] map;

    private int level; 
    private int height;
    private int width;
    private int strength=0;

    private int startPointX=0, startPointY=0;
    private boolean isNewGame = false;
    private boolean isKeyPressed = false;
    private boolean hasSpecialItem;
    private boolean isSkipped = false;
    private boolean isTwoPage = false;
    private boolean isTwoPageSkip = false;
    private boolean isTrapped = false;
    private static boolean north, south, west, east;

    private int[][][] numberMap; 


    private long previousTime = 0;
    private long tmpTime = 0;
    private long textTime = 0;
    private long textTime2 = 0;

    private AnimationTimer gameLoop;
    private AnimationTimer labelTimer;
    private AnimationTimer fadeInTimer;
    private AnimationTimer fadeOutTimer;
    private AnimationTimer textTimer;
    private AnimationTimer textTimer2;

    private AnimationTimer pauseTimer = new AnimationTimer() {

        @Override
        public void handle(long now) {
            if(previousTime == 0) previousTime = now;
            if(now-previousTime > 250000000) {
                isKeyPressed = false;
                north = false;  
                east = false;
                south = false;
                west = false;
                previousTime = 0;
                pauseTimer.stop();
            }
        }
    };

    public GameLevel(int level, int height, int width, int streng, int[][][] m) {

        this.level = level;
        this.height = height;
        this.width = width;
        this.numberMap = m;
        this.strength = streng;
        hasSpecialItem = Elephant.levelSpecial[level];

        scene = new Scene(root, 1152, 648, Color.BLACK);
        scene.getStylesheets().add(this.getClass().getResource("font.css").toExternalForm());

        addKeyPressListener();

        setBackButton();
        transformMap();
        setMapProperties();
        setCharacter();
        setEndGameLabel();
        setLabel();

        setSpotImageView();
        

        // game loop setting
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // potato walk or wink
                potatoWalk();

                if(map[potato.Y][potato.X].isDangered && !isTrapped) {
                    isTrapped = true;
                    strengthDecrease(1);
                }

                // if potato's strength is zero -> restart
                if(strength < 0 && isNewGame==false) {
                    isNewGame = true;
                    // failLabel.setVisible(true);
                    showLabel(false);
                }

                // if potato is on the end point -> switch to next level
                if(map[potato.Y][potato.X].isEnd && hasSpecialItem && isNewGame==false) {
                    isNewGame = true;
                    // completeLabel.setVisible(true);
                    showLabel(true);

                    if(level <= 36) Elephant.levelStatus[level+1] = 1;
                    if(level == 12) Elephant.episdoeStatus[2] = 1;
                    if(level == 21) Elephant.episdoeStatus[3] = 1;
                    try {
                        Elephant.writeLevelInfo(false);
                        Elephant.readLevelInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // if potato gets the special item
                if(numberMap[level][potato.Y][potato.X]==5 && hasSpecialItem==false) {
                    hasSpecialItem = true;
                    map[potato.Y][potato.X].imageView.setImage(Elephant.mapImage[0]);
                }
            }
        };

        // game loop start
        gameLoop.start();
    }


    private void setBackButton() {
        // set back button
        ImageView backArrow = new ImageView(Elephant.back);
        backArrow.setPreserveRatio(true);
        backArrow.setLayoutX(24);
        backArrow.setLayoutY(24);
        backArrow.setFitWidth(80);
        backArrow.setFitHeight(55);
        root.getChildren().add(backArrow);
        
        backButton = new Button();
        backButton.setLayoutX(24);
        backButton.setLayoutY(24);
        backButton.setPrefSize(80, 55);
        backButton.setOpacity(0);
        backButton.setOnAction( (event) -> {
            gameLoop.stop();

            try {
                Elephant.readLevelInfo();
            } catch (Exception e) {
                System.out.println("Loading LevelInfo failed.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("episode.fxml"));
            Stage backStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Parent backRoot = null;
            try {
                backRoot = loader.load();
            } catch (IOException e) {
                System.out.println("level.fxml loading failed");
            }
            backStage.setScene(new Scene(backRoot));

            EpisodeController controller = loader.getController();
            controller.settingLock();
        });
        root.getChildren().add(backButton);
    }


    public void transformMap() {

        map = new Chunk[height][width];

        for(int i=0 ; i<height ; i++) {
            for(int j=0 ; j<width ; j++) {
                map[i][j] = new Chunk();

                map[i][j].setImageView(Elephant.mapImage[numberMap[level][i][j]]);

                map[i][j].imageView.setFitWidth(64);
                map[i][j].imageView.setFitHeight(64);
                map[i][j].imageView.setY((648-height*64)/2 + 64*i);
                map[i][j].imageView.setX((1152-width*64)/2 + 64*j);

                root.getChildren().add(map[i][j].imageView);
            }
        }
    }

    public void setMapProperties() {
        ImageView  decorationView = new ImageView();

        decorationView.setY((648-height*64)/2);
        decorationView.setX((1152-width*64)/2);
        root.getChildren().add(decorationView);
        if(level == 14) decorationView.setImage(Elephant.map2_2);
        if(level == 15) decorationView.setImage(Elephant.map2_3);
        if(level == 16) decorationView.setImage(Elephant.map2_4);
        if(level == 17) decorationView.setImage(Elephant.map2_5);
        if(level == 18) decorationView.setImage(Elephant.map2_6);
        if(level == 19) decorationView.setImage(Elephant.map2_7);
        if(level == 20) decorationView.setImage(Elephant.map2_8);
        if(level == 21) decorationView.setImage(Elephant.map2_9);

        for(int i=0 ; i<height ; i++) {
            for(int j=0 ; j<width ; j++) {

                if(numberMap[level][i][j] == 0) {
                    if(level>12) map[i][j].setImageView(Elephant.pavement);
                }

                // wall
                if(numberMap[level][i][j]==1 ) {
                    map[i][j].setBlocked(true);
                    if(level>12) map[i][j].setImageView(Elephant.wall);
                }

                // box
                if(numberMap[level][i][j] == 4) {
                    map[i][j].setBlocked(true);
                    map[i][j].makeBox(i, j, height, width);
                    if(level>12) map[i][j].setImageView(Elephant.pavement);
                    if(level>12 && level!=18) map[i][j].box.boxView.setImage(Elephant.box);
                    if(level == 16) map[i][j].box.boxView.setImage(Elephant.stone);
                    
                    root.getChildren().add(map[i][j].box.boxView);
                }

                // special item
                if(numberMap[level][i][j] == 5) {
                    map[i][j].setSpecial(true);
                    map[i][j].subView = new ImageView();
                    map[i][j].subView.setY((648-height*64)/2 + 64*i);
                    map[i][j].subView.setX((1152-width*64)/2 + 64*j);
                    root.getChildren().add(map[i][j].subView);

                    if(level == 1) map[i][j].setImageView(Elephant.special1);
                    if(level == 7) map[i][j].setImageView(Elephant.special7);
                    if(level == 9) map[i][j].setImageView(Elephant.special9);
                    if(level == 10) map[i][j].setImageView(Elephant.special7);
                    if(level == 11) map[i][j].setImageView(Elephant.special11);
                }

                // trap
                if(numberMap[level][i][j] == 2) {
                    map[i][j].makeTrap(i, j, height, width, false);
                    root.getChildren().add(map[i][j].trap.trapView);
                }
                if(numberMap[level][i][j] == 3) {
                    map[i][j].makeTrap(i, j, height, width, true);
                    root.getChildren().add(map[i][j].trap.trapView);
                }

                if(numberMap[level][i][j] == 7) {
                    map[i][j].setBlocked(true);
                    if(level > 12) map[i][j].imageView.setImage(Elephant.fireHyrant);

                    map[i][j].subView = new ImageView();
                    map[i][j].subView.setY((648-height*64)/2 + 64*i);
                    map[i][j].subView.setX((1152-width*64)/2 + 64*j);
                    root.getChildren().add(map[i][j].subView);

                    // map[i][j].setImageView(Elephant.blackImage);
                }

                // end
                if(numberMap[level][i][j] == 9) {
                    map[i][j].subView = new ImageView();
                    map[i][j].subView.setY((648-height*64)/2 + 64*i);
                    map[i][j].subView.setX((1152-width*64)/2 + 64*j);
                    root.getChildren().add(map[i][j].subView);

                    if(level == 1) map[i][j].setImageView(Elephant.end1);
                    if(level == 2) map[i][j].setImageView(Elephant.end2);
                    if(level == 3) map[i][j].setImageView(Elephant.end3);
                    if(level == 4) map[i][j].setImageView(Elephant.end4);
                    if(level == 5) map[i][j].setImageView(Elephant.end5);
                    if(level == 6) map[i][j].setImageView(Elephant.end6);
                    if(level == 7) map[i][j].setImageView(Elephant.end4);
                    if(level == 8) map[i][j].setImageView(Elephant.end8);
                    if(level == 9) map[i][j].setImageView(Elephant.end8);
                    if(level == 10) map[i][j].setImageView(Elephant.end4);
                    if(level == 11) map[i][j].setImageView(Elephant.end4);
                    if(level == 12) map[i][j].setImageView(Elephant.end12);
                    if(level == 13) map[i][j].setImageView(Elephant.end13);
                    map[i][j].setEnd(true);
                }

                if(numberMap[level][i][j] == 10) {
                    if(level>12) map[i][j].setImageView(Elephant.pavement);
                    map[i][j].setStart(true);
                    startPointY = i;
                    startPointX = j;
                }
            }
        }
    }

    private void setCharacter() {
        switch(Elephant.levelCharacter[level]) {
            case 1: potato = new Character(new ImageView(Elephant.salman)); break;
            case 2: potato = new Character(new ImageView(Elephant.gura)); break;
            case 3: potato = new Character(new ImageView(Elephant.stacy)); break;
            case 4: potato = new Character(new ImageView(Elephant.teacher)); break;
            case 5: potato = new Character(new ImageView(Elephant.worker)); break;
            case 6: potato = new Character(new ImageView(Elephant.dreamedGura)); break;
            case 7: potato = new Character(new ImageView(Elephant.dreamedTeacher)); break;
        }

        potato.animation.play();
        potato.setLayoutX((1152-width*64)/2 + 64*startPointX);
        potato.setLayoutY((648-height*64)/2 + 64*startPointY);
        potato.setChunk(startPointY, startPointX);
        root.getChildren().add(potato);        
    }

    private void setEndGameLabel() {
        completeLabel = new Label();
        completeLabel.setPrefSize(550, 280);
        completeLabel.setLayoutX(301);
        completeLabel.setLayoutY(184);
        completeLabel.setStyle("-fx-background-image:url(\"resources/Images/levelComplete.png\")");
        completeLabel.setVisible(false);
        root.getChildren().add(completeLabel);

        failLabel = new Label();
        failLabel.setPrefSize(550, 280);
        failLabel.setLayoutX(301);
        failLabel.setLayoutY(184);
        failLabel.setStyle("-fx-background-image:url(\"resources/Images/levelFail.png\")");
        failLabel.setVisible(false);
        root.getChildren().add(failLabel);
    }

    private void setLabel() {
        stepLabel1 = new Label();
        stepLabel1.setText("             "+Integer.toString(strength));
        stepLabel1.setAlignment(Pos.CENTER);
        stepLabel1.setPrefSize(230, 80);
        stepLabel1.setLayoutX(902);
        stepLabel1.setLayoutY(548);
        stepLabel1.setTextFill(Color.web("#1c5776"));
        stepLabel1.setFont(new Font("Rockwell", 36));
        stepLabel1.setStyle("-fx-background-color : #fbaa88;" + "-fx-background-radius:40;");
        root.getChildren().add(stepLabel1);

        stepLabel2 = new Label();
        stepLabel2.setText("STEPS");
        stepLabel2.setAlignment(Pos.CENTER);
        stepLabel2.setPrefSize(120, 60);
        stepLabel2.setLayoutX(925);
        stepLabel2.setLayoutY(558);
        stepLabel2.setTextFill(Color.web("#FBAA88"));
        stepLabel2.setFont(new Font("Rockwell", 36));
        stepLabel2.setStyle("-fx-background-color : #e9ddb6;" + "-fx-background-radius:30;");
        root.getChildren().add(stepLabel2);

        levelLabel = new Label();
        levelLabel.setText("LEVEL "+level);
        levelLabel.setAlignment(Pos.CENTER);
        levelLabel.setPrefSize(220, 80);
        levelLabel.setLayoutX(50);
        levelLabel.setLayoutY(284);
        levelLabel.setTextFill(Color.web("#003128"));
        levelLabel.setFont(new Font("Rockwell", 42));
        levelLabel.setStyle("-fx-background-color : #b6545e;" + "-fx-background-radius:30;");
        root.getChildren().add(levelLabel);
    }

    private void setSpotImageView() {
        
        spot = new ImageView(Elephant.diary);
        spot.setLayoutX(0);
        spot.setLayoutX(0);
        spot.setVisible(false);
        root.getChildren().add(spot);

        String tmpText1="", tmpText2="";
        if(LevelText.text[level].length() > 120) {
            tmpText1 = LevelText.text[level].substring(0, 120);
            if(LevelText.text[level].length() >= 220)
                tmpText2 = LevelText.text[level].substring(120,220);

            else 
                tmpText2 = LevelText.text[level].substring(120);
        }
        else tmpText1 = LevelText.text[level];

        final String text1 = tmpText1;
        final String text2 = tmpText2;

        spotLabel1 = new Label();
        spotLabel1.setId("spotLabel");
        spotLabel1.setText("");
        spotLabel1.setWrapText(true);
        spotLabel1.setTextAlignment(TextAlignment.JUSTIFY);
        spotLabel1.setMaxWidth(260);
        spotLabel1.setRotate(-6);
        spotLabel1.setLayoutX(280);
        spotLabel1.setLayoutY(120);
        spotLabel1.setVisible(false);
        root.getChildren().add(spotLabel1);


        spotLabel2 = new Label();
        spotLabel2.setId("spotLabel");
        spotLabel2.setText("");
        spotLabel2.setWrapText(true);
        spotLabel2.setTextAlignment(TextAlignment.JUSTIFY);
        spotLabel2.setMaxWidth(260);
        spotLabel2.setRotate(-6);
        spotLabel2.setLayoutX(550);
        spotLabel2.setLayoutY(95);
        spotLabel2.setVisible(false);
        root.getChildren().add(spotLabel2);

        skip = new Label();
        skip.setId("spotLabel");
        skip.setText("Click anywhere to skip...");
        skip.setLayoutX(620);
        skip.setLayoutY(430);
        skip.setRotate(-6);
        skip.setVisible(false);
        root.getChildren().add(skip);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent e) {

                if(spot.isVisible() == false) return;

                if(isSkipped == false) {
                    textTimer.stop();
                    textTimer2.stop();

                    if(LevelText.text[level].length() > 220) {
                        if(isTwoPage == false) {
                            spotLabel1.setText(text1);
                            spotLabel2.setText(text2);

                            isTwoPage = true;
                            return;
                        }
                        else {
                            if(isTwoPageSkip == false) {
                                spotLabel1.setText("");
                                spotLabel2.setText("");

                                isTwoPageSkip = true;
                                textTimer2.start();
                            }
                            else {
                                if(LevelText.text[level].length() >= 340) {
                                    spotLabel1.setText(LevelText.text[level].substring(220, 340));
                                    spotLabel2.setText(LevelText.text[level].substring(340));
                                }
                                else spotLabel1.setText(LevelText.text[level].substring(220));

                                isSkipped = true;
                                return;
                            }
                        }
                    }

                    else
                    {
                        isSkipped = true;

                        spotLabel1.setText(text1);
                        spotLabel2.setText(text2);

                        return;
                    }
                }
                if(spot.isVisible() && isSkipped) {
                    spot.setVisible(false);
                    spotLabel1.setVisible(false);
                    spotLabel2.setVisible(false);
                    skip.setVisible(false);
                    GameLevel game = new GameLevel(level+1 ,Elephant.levelHeight[level+1], Elephant.levelWidth[level+1], Elephant.levelStrength[level+1], Elephant.mapInfo);
                    Elephant.stage.setScene(game.scene);
                };
            }
        });
    }

    private void addKeyPressListener() {

        scene.setOnKeyPressed(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent e) {
                if(isKeyPressed == false) {
                    KeyCode in = e.getCode();

                    if(in == KeyCode.R) {
                        gameLoop.stop();
                        GameLevel game = new GameLevel(level ,Elephant.levelHeight[level], Elephant.levelWidth[level], Elephant.levelStrength[level], Elephant.mapInfo);
                        game.spot.setVisible(false);
                        Elephant.stage.setScene(game.scene);
                    }

                    if(in == KeyCode.W) {

                        if(map[potato.Y-1][potato.X].isBlocked && map[potato.Y-1][potato.X].box==null) {
                            potato.direction = 'W';
                            return;
                        }

                        boolean isBoxMoved = (map[potato.Y-1][potato.X].box!=null && map[potato.Y-1][potato.X].box.moveNorth(map));

                        if(isBoxMoved || !map[potato.Y-1][potato.X].isBlocked && !isBoxMoved) {
                            for(int i=0 ; i<height ; i++) {
                                for(int j=0 ; j<width ; j++) {
                                    if(map[i][j].trap != null) {
                                        map[i][j].trap.changeStatus();
                                        if(map[i][j].trap.isTrapOn) map[i][j].isDangered = true;
                                        else map[i][j].isDangered = false;
                                    }
                                }
                            }
                        }
                        if(isBoxMoved) {
                            strengthDecrease(1);
                        }
                        if(!map[potato.Y-1][potato.X].isBlocked && !isBoxMoved) {

                            strengthDecrease(1);

                            isKeyPressed = true;
                            north = true;
                            potato.Y--;
                            potato.deltaDistance = 0;

                            if(!map[potato.Y][potato.X].isDangered) isTrapped = false;
                            pauseTimer.start();
                        }

                    }

                    else if(in == KeyCode.S) {

                        if(map[potato.Y+1][potato.X].isBlocked && map[potato.Y+1][potato.X].box==null) {
                            potato.direction = 'S';
                            return;
                        }

                        boolean isBoxMoved = (map[potato.Y+1][potato.X].box!=null&&map[potato.Y+1][potato.X].box.moveSouth(map));

                        if(isBoxMoved || !map[potato.Y+1][potato.X].isBlocked) {
                            for(int i=0 ; i<height ; i++) {
                                for(int j=0 ; j<width ; j++) {
                                    if(map[i][j].trap != null) {
                                        map[i][j].trap.changeStatus();
                                        if(map[i][j].trap.isTrapOn) map[i][j].isDangered = true;
                                        else map[i][j].isDangered = false;
                                    }
                                }
                            }
                        }

                        if(isBoxMoved) {
                            strengthDecrease(1);
                            return;
                        }
                        if(!map[potato.Y+1][potato.X].isBlocked) {

                            strengthDecrease(1);

                            isKeyPressed = true;
                            south = true;
                            potato.Y++;
                            potato.deltaDistance = 0;

                            if(!map[potato.Y][potato.X].isDangered) isTrapped = false;
                            pauseTimer.start();
                        }                            
                    }

                    else if(in == KeyCode.A) {

                        if(map[potato.Y][potato.X-1].isBlocked && map[potato.Y][potato.X-1].box==null) {
                            potato.direction = 'A';
                            return;
                        }

                        boolean isBoxMoved = (map[potato.Y][potato.X-1].box!=null && map[potato.Y][potato.X-1].box.moveWest(map));
                        if(isBoxMoved || !map[potato.Y][potato.X-1].isBlocked) {
                            for(int i=0 ; i<height ; i++) {
                                for(int j=0 ; j<width ; j++) {
                                    if(map[i][j].trap != null) {
                                        map[i][j].trap.changeStatus();
                                        if(map[i][j].trap.isTrapOn) map[i][j].isDangered = true;
                                        else map[i][j].isDangered = false;
                                    }
                                }
                            }
                        }

                        if(isBoxMoved) {
                            strengthDecrease(1);
                            return;
                        }
                        if(!map[potato.Y][potato.X-1].isBlocked) {

                            strengthDecrease(1);

                            isKeyPressed = true;
                            west = true;
                            potato.X--;
                            potato.deltaDistance = 0;

                            if(!map[potato.Y][potato.X].isDangered) isTrapped = false;
                            pauseTimer.start();
                        }
                    }

                    else if(in == KeyCode.D) {
                        
                        if(map[potato.Y][potato.X+1].isBlocked&&map[potato.Y][potato.X+1].box==null) {
                            potato.direction = 'D';
                            return;
                        }

                        boolean isBoxMoved = (map[potato.Y][potato.X+1].box!=null && map[potato.Y][potato.X+1].box.moveEast(map));
                        if(isBoxMoved || !map[potato.Y][potato.X+1].isBlocked) {
                            for(int i=0 ; i<height ; i++) {
                                for(int j=0 ; j<width ; j++) {
                                    if(map[i][j].trap != null) {
                                        map[i][j].trap.changeStatus();
                                        if(map[i][j].trap.isTrapOn) map[i][j].isDangered = true;
                                        else map[i][j].isDangered = false;
                                    }
                                }
                            }
                        }

                        if(isBoxMoved) {
                            strengthDecrease(1);
                            return;
                        }
                        if(!map[potato.Y][potato.X+1].isBlocked) {

                            strengthDecrease(1);

                            isKeyPressed = true;
                            east = true;
                            potato.X++;
                            potato.deltaDistance = 0;

                            if(!map[potato.Y][potato.X].isDangered) isTrapped = false;
                            pauseTimer.start();
                        }
                    }
                }
            }
        });
    }



    private void showLabel(boolean newGame) {

        blackView = new ImageView(Elephant.blackImage);
        blackView.setLayoutX(0);
        blackView.setLayoutY(0);
        blackView.setOpacity(0);
        root.getChildren().add(blackView);

        fadeInTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(tmpTime == 0) tmpTime = now;
                if(now - tmpTime <= 1.0e9) {
                    blackView.setOpacity((now-tmpTime)/1.0e9);
                }
                else {
                    tmpTime = 0;
                    fadeOutTimer.start();
                    fadeInTimer.stop();
                }
            }
        };

        fadeOutTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if(tmpTime == 0) {
                    showText();
                    tmpTime = now; 
                }

                if(now - tmpTime <= 1.0e9) {
                    blackView.setOpacity(1-(now-tmpTime)/1.0e9);
                }
                else fadeOutTimer.stop();
            }
            
        };

        labelTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                isKeyPressed = true;

                if(previousTime == 0) previousTime=now;
                if(now-previousTime >= 0.5e9 && now-previousTime < 2.5e9) {
                    if(newGame == true) completeLabel.setVisible(true);
                    else failLabel.setVisible(true);
                }

                if(now-previousTime >= 2.5e9) {
                    if(newGame == true) {
                        fadeInTimer.start();
                        labelTimer.stop();
                        gameLoop.stop();
                    }
                    else {
                        failLabel.setVisible(false);

                        GameLevel game = new GameLevel(level ,Elephant.levelHeight[level], Elephant.levelWidth[level], Elephant.levelStrength[level], Elephant.mapInfo);
                        game.spot.setVisible(false);
                        Elephant.stage.setScene(game.scene);

                        labelTimer.stop();
                        gameLoop.stop();
                    }
                }
            }
        };
        labelTimer.start();
    }

    public void setEnd(int y, int x) {
        map[y][x].isEnd = true;
    }

    private void strengthDecrease(int n) {
        strength -= n;
        if(strength >= 0) 
            stepLabel1.setText("             "+Integer.toString(strength));
    }

    private void potatoWalk() {

        int dx = 0, dy = 0;

        // determine potato is walking or not
        if (north) dy -= 1;
        if (south) dy += 1;
        if (east)  dx += 1;
        if (west)  dx -= 1;

        // potato is stop -> wink 
        if(dx == 0 && dy == 0) potato.wink();
        else {
            potato.moveX(dx);
            potato.moveY(dy);
        }
    }

    private void showText(){


        spot.setVisible(true);
        spotLabel1.setVisible(true);
        spotLabel2.setVisible(true);
        skip.setVisible(true);

        textTimer = new AnimationTimer(){
            int cnt = 1;
            int tmp = 1;

            @Override
            public void handle(long now) {
                if((tmp++)%4 != 0) return;

                if(textTime == 0) textTime = now; 
                if(now - textTime <= 8.3e9) {
                    if(cnt > LevelText.text[level].length()) {
                        isSkipped = true;
                        return;
                    }
                    else if(cnt <= 120) 
                        spotLabel1.setText(LevelText.text[level].substring(0, cnt++));                        
                }
                else {
                    if(cnt > 220 || cnt>LevelText.text[level].length()) {
                        if(LevelText.text[level].length() < cnt) isSkipped = true;
                        isTwoPage = true;
                        textTimer.stop();
                    }

                    else {
                        spotLabel2.setText(LevelText.text[level].substring(120, cnt++));                        
                    }
                }
            }
        };
        textTimer.start();

        textTimer2 = new AnimationTimer(){
        
            int cnt = 221;
            int tmp = 1;

            @Override
            public void handle(long now) {
                if((tmp++)%4 != 0) return;

                if(textTime2 == 0) textTime2 = now; 
                if(now - textTime2 <= 8.3e9) {
                    if(cnt > LevelText.text[level].length()) {
                        isSkipped = true;
                        return;
                    }
                    else if(cnt <= 340) 
                        spotLabel1.setText(LevelText.text[level].substring(220, cnt++));                        
                }
                else {
                    if(cnt > LevelText.text[level].length()) {
                        isSkipped = true;
                        textTimer2.stop();
                    }

                    else {
                        spotLabel2.setText(LevelText.text[level].substring(340, cnt++));                        
                    }
                }
            }
        };
    }
}
