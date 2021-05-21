import java.io.IOException;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LevelController {

    @FXML
    Label red1, red2, red3, red4, red5, red6, red7, red8, red9, red10, red11, red12, 
          red13, red14, red15, red16, red17, red18, red19, red20, red21, red22, red23, red24;

    @FXML
    Label blue1, blue2, blue3, blue4, blue5, blue6, blue7, blue8, blue9, blue10, blue11, blue12, 
          blue13, blue14, blue15, blue16, blue17, blue18, blue19, blue20, blue21, blue22, blue23, blue24;

        
    public List<Label> redLabel = new ArrayList<Label>();
    public List<Label> blueLabel = new ArrayList<Label>();


    public void back(ActionEvent e) throws IOException {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("begin.fxml"));
        stage.setScene(new Scene(root));
    }

    public void button1(ActionEvent e) throws IOException {
        System.out.println("aaaa");
    }
    public void button2(ActionEvent e) throws IOException {
        System.out.println("bbb");
    }

    public void settingLock() {
        settingArray();

        for(int i=0 ; i<24 ; i++) {
            if(Dungeon.levelStatus[i] == 1) blueLabel.get(i).setVisible(true);
            else redLabel.get(i).setVisible(true);
        }
    }

    public void settingArray() {
        redLabel.add(red1);      blueLabel.add(blue1);
        redLabel.add(red2);      blueLabel.add(blue2);
        redLabel.add(red3);      blueLabel.add(blue3);
        redLabel.add(red4);      blueLabel.add(blue4);
        redLabel.add(red5);      blueLabel.add(blue5);
        redLabel.add(red6);      blueLabel.add(blue6);
        redLabel.add(red7);      blueLabel.add(blue7);
        redLabel.add(red8);      blueLabel.add(blue8);
        redLabel.add(red9);      blueLabel.add(blue9);
        redLabel.add(red10);     blueLabel.add(blue10);
        redLabel.add(red11);     blueLabel.add(blue11);
        redLabel.add(red12);     blueLabel.add(blue12);
        redLabel.add(red13);     blueLabel.add(blue13);
        redLabel.add(red14);     blueLabel.add(blue14);
        redLabel.add(red15);     blueLabel.add(blue15);
        redLabel.add(red16);     blueLabel.add(blue16);
        redLabel.add(red17);     blueLabel.add(blue17);
        redLabel.add(red18);     blueLabel.add(blue18);
        redLabel.add(red19);     blueLabel.add(blue19);
        redLabel.add(red20);     blueLabel.add(blue20);
        redLabel.add(red21);     blueLabel.add(blue21);
        redLabel.add(red22);     blueLabel.add(blue22);
        redLabel.add(red23);     blueLabel.add(blue23);
        redLabel.add(red24);     blueLabel.add(blue24);
    }
}