import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class runtm {

    public static void main(String[] args) throws Exception {

        int statesNum = 0;
        String acceptS = "";
        String rejectS = "";
        List<String> states = new ArrayList<String>();
        List<String> alphabets = new ArrayList<String>();
        String[] tape = new String[100000];
        List<String[]> transitions = new ArrayList<String[]>(); 

        String descriptionFile = "";
        String tapeFile = "";
        String line;
        String[] lineSplit;

        int stepsNum;
        int currentPos;
        String currentS = "";
        String currentSym = "";
        String direction;
        String tapeS = "";

        /* ---- Part 1: validity checks ---- */

        //checks the number of files provided
        if(args.length == 2){
            descriptionFile = args[0];
            tapeFile = args[1];
        } else if (args.length == 1){
            descriptionFile = args[0];

            File file = new File("tm_tape.txt");

            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } else{
            System.out.println("Insufficient arguments provided");
        }

        //prints the contents of both files
        System.out.println("Turing Machine configuration: ");
        printFile(descriptionFile);

        System.out.println();
        
        System.out.println("Tape: ");
        printFile(tapeFile);
        
        

        try {
            FileReader fileReader = new FileReader(descriptionFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            lineSplit = line.split("\\s+");
            
            //checks whether the states line is valid
            if(lineSplit[0].equals("states") && lineSplit.length == 2){
                try{
                    statesNum = Integer.parseInt(lineSplit[1]);
                    if(!(statesNum >= 2)){
                        System.out.println("input error");
                        System.exit(2);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("input error");
                    System.exit(2);
                }
            } else {
                System.out.println("input error");
                System.exit(2);
            }

            currentS = bufferedReader.readLine();
            states.add(currentS);

            //checks whether all the states are valid
            for(int counter = 1; counter < statesNum; counter++){

                line = bufferedReader.readLine();
                lineSplit = line.split("\\s+"); 

                if(lineSplit[0].equals("alphabets") || lineSplit[0].equals("+") || lineSplit[0].equals("-")){
                    System.out.println("input error");
                    System.exit(2);
                }

                //adds all the states to a list
                if(lineSplit.length == 1){
                    states.add(line);
                } else if(lineSplit.length == 2){
                    if(lineSplit[1].equals("+")){
                        acceptS = lineSplit[0];
                        states.add(acceptS);
                    } else if(lineSplit[1].equals("-")){
                        rejectS = lineSplit[0];
                        states.add(rejectS);
                    } else{
                        System.out.println("input error");
                        System.exit(2);
                    }
                } else{
                    System.out.println("input error");
                    System.exit(2);
                }
            } 
            
            if(states.size() != statesNum){
                System.out.println("input error");
                System.exit(2);
            }

            line = bufferedReader.readLine();
            lineSplit = line.split("\\s+"); 

            //checks if the alphabet line is valid
            if(lineSplit[0].equals("alphabet") && lineSplit.length >= 1){
                if(line.contains("_")){
                    System.out.println("input error");
                    System.exit(2);
                } 
            } else{
                System.out.println("input error");
                System.exit(2);
            }

            for(int counter = 1; counter < lineSplit.length; counter++){
                alphabets.add(lineSplit[counter]);
            }

            //adds all the symbols to a list
            while ((line = bufferedReader.readLine()) != null) {
                lineSplit = line.split("\\s+"); 

                if(!states.contains(lineSplit[0])){System.out.println("input error"); System.exit(2);}
                if(!states.contains(lineSplit[2])){System.out.println("input error"); System.exit(2);}
                if(!alphabets.contains(lineSplit[1]) && !lineSplit[1].equals("_")){System.out.println("input error"); System.exit(2);}
                if(!alphabets.contains(lineSplit[3]) && !lineSplit[3].contains("_")){System.out.println("input error"); System.exit(2);}

                if(!(lineSplit[4].equals("L") || lineSplit[4].equals("R") || lineSplit[4].equals("N"))){
                    System.out.println("input error");
                    System.exit(2);
                }

                transitions.add(lineSplit);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }

        //gets the contents of the tape and stores it in an array 
        if(args.length == 2){
            Path path = Paths.get(tapeFile);
            try{
                String contents = Files.readString(path).replaceAll("\\s+", "");

                if(contents  == null){
                    contents = "_";
                }

                char[] chars = contents.toCharArray();
                String[] strings = new String[chars.length];
                
                for(int counter = 0; counter < chars.length; counter++){
                    boolean valid = false;
                    strings[counter] = String.valueOf(chars[counter]);
                
                    for(int counter2 = 0; counter2 < alphabets.size(); counter2++){
                        if(strings[counter].equals(alphabets.get(counter2)) || strings[counter].equals("_")){
                            valid = true;
                        }
                    }

                    if(!valid){
                        System.out.println("input error");
                        System.exit(2);
                    }
                }

                System.arraycopy(strings, 0, tape, 0, strings.length);
                currentSym = tape[0];


            } catch (IOException e) {
                e.printStackTrace();
                System.exit(3);
            }
        }
        else{
            tape[0] = "_";
        }

        /* ---- Part 2: Turing Machine simulation ---- */

        currentPos = 0;
        stepsNum = 0;
        direction = "";
        boolean found;

        do{
            found = false;  
            for(int counter = 0; counter < transitions.size(); counter++){
                String newS = transitions.get(counter)[0];
                String newSym = transitions.get(counter)[1];
                direction = transitions.get(counter)[4];
                if(currentS.equals(newS) && currentSym.equals(newSym)){
                    found = true;
                    stepsNum++;
                    currentS = transitions.get(counter)[2];
                    tape[currentPos] = transitions.get(counter)[3];

                    if(direction.equals("L")){
                        if(currentPos != 0){
                            currentPos--;
                        }
                        currentSym = tape[currentPos];
                    } else if(direction.equals("R")){
                        currentPos++;
                        if(tape[currentPos] == null){
                            tape[currentPos] = "_";
                        }
                        currentSym = tape[currentPos];
                    }

                    //System.out.println(tapeS);

                    if(currentS.equals(acceptS)){
                        tapeS = getTapeState(tape);
                        System.out.println("accepted\n" + (stepsNum - 1) + "\n" + tapeS);
                        System.exit(0);
                    } else if(currentS.equals(rejectS)){
                        tapeS = getTapeState(tape);
                        System.out.println("not accepted\n" + (stepsNum - 1) + "\n" + tapeS);
                        System.exit(1);
                    }
                }
            }
        } while(found);

        if(!found){
            tapeS = getTapeState(tape);
            System.out.println("not accepted\n"+ stepsNum + "\n" + tapeS);
        }
    }

    //function to print out all the contents of a file
    static void printFile(String fileName){

        String line;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to get the current state of the tape as a string
    static String getTapeState(String[] tape){

        int counter = 0;
        String tempTapeS = "";
        String tapeS = "";
        String tempChar;

        boolean hasSym = false;
        while(tape[counter] != null){
            tempTapeS = tempTapeS + tape[counter];
            if(!tape[counter].equals("_")){
                hasSym = true;
            }
            counter++;
        }

        if(!hasSym){
            tapeS = "_";
            return tapeS;
        }

        boolean lastChar = false;
        do{
            counter--;
            tempChar = Character.toString(tempTapeS.charAt(counter));
            if(!tempChar.equals("_")){
                lastChar = true;
            }
        } while(!lastChar && counter != 0);

        while(counter != -1){
            tempChar = Character.toString(tempTapeS.charAt(counter));
            tapeS = tempChar + tapeS;
            counter--;
        }

        return tapeS;
    }
}