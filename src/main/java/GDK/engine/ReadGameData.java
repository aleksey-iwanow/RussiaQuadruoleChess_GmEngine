package GDK.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadGameData {
    public String fileName;

    public ReadGameData(String fileName){
        this.fileName = System.getProperty("user.dir") + "\\" + fileName;
    }

    public static GameObject readPrefab(String fileName) throws IOException {
        String path = System.getProperty("user.dir") + "\\" + Config.PATH_STATIC + fileName;
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line = br.readLine();
            ArrayList<String> currentLines = new ArrayList<>();
            while (line != null) {
                currentLines.add(line);
                if (currentLines.size() == 10){
                    return new GameObject(currentLines, Engine.gameObjects.size());
                }
                line = br.readLine();
            }
            return null;

        } catch (IOException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public ArrayList<GameObject> readFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName));){
            String line = "s";
            ArrayList<GameObject> gmObjectsAwake = new ArrayList<>();
            ArrayList<String> currentLines = new ArrayList<String>();
            while (true) {
                line = br.readLine();
                if (line == null) break;
                if (!line.equals("~") && !line.equals("(~&~)")){
                    currentLines.add(line);
                }
                else {
                    gmObjectsAwake.add(new GameObject(currentLines, gmObjectsAwake.size()));
                    currentLines.clear();
                }
            }
            return gmObjectsAwake;

        } catch (IOException ex){
            throw new IOException(ex.getMessage());
        }
    }
}
