/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ospart2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author Ahmad Horyzat
 */
public class OSPart2 {

    /**
     * @param args the command line arguments
     */
    static Process [] processes = new Process[5];
    static String[] memory;
    static int framesNum;
    static int freeFramesNum;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        int mSize = 0;
        int pSize = 0;
        int Q = 0;
        int CS = 0;
        int processId;
        int arrivalTime;
        int cpuBurst;
        int processSize;
        
        File file = new File("Processes.txt");
        try {
            Scanner infile = new Scanner(file);
            
            mSize = infile.nextInt();
            pSize = infile.nextInt();
            Q = infile.nextInt();
            CS = infile.nextInt();
            for(int i = 0; i< 5; i++){
                processId = infile.nextInt();
                arrivalTime = infile.nextInt();
                cpuBurst = infile.nextInt();
                processSize = infile.nextInt();
                processes[i] = new Process(processId, arrivalTime, cpuBurst, processSize);
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
        
        ArrayList<Process> arrivingProcesses = new ArrayList<>();
        arrivingProcesses.addAll(Arrays.asList(processes));
        arrivingProcesses = sortArrival(arrivingProcesses);

        int[] process0Table = new int[(int)Math.ceil(processes[0].getSize()*1.0 /pSize)];
        int[] process1Table = new int[(int)Math.ceil(processes[1].getSize()*1.0 /pSize)];
        int[] process2Table = new int[(int)Math.ceil(processes[2].getSize()*1.0 /pSize)];
        int[] process3Table = new int[(int)Math.ceil(processes[3].getSize()*1.0 /pSize)];
        int[] process4Table = new int[(int)Math.ceil(processes[4].getSize()*1.0 /pSize)];
        
        framesNum = (int)Math.ceil(mSize * 1.0 / pSize);
        memory = new String[framesNum];
        
        for (int i = 0; i < memory.length; i++){
            memory[i] = "free";
        }
        freeFramesNum = memory.length;

        int[][] arr = new int[5][];  // array that sore the addresses of the 5 tables
        arr[0] = process0Table;
        arr[1] = process1Table;
        arr[2] = process2Table;
        arr[3] = process3Table;
        arr[4] = process4Table;
        
        //initialize all tables to -1
        for (int[] table : arr) {
            for (int j = 0; j < table.length; j++) {
                table[j] = -1;
            }
        }
        
        ArrayList<Integer> PinM = new ArrayList<>(); //list of processes loaded to memory
        while(!arrivingProcesses.isEmpty()) {
            int index = arrivingProcesses.get(0).getProcessId();
            int pagesNum = (int)Math.ceil(processes[index].getSize()*1.0 /pSize);
            System.out.println("The arrived Process is  P" + index);
            System.out.println("This process has " + pagesNum + " pages");
            System.out.println("The free frames in memory equals: " + freeFramesNum);
            if( freeFramesNum > pagesNum) {
                fillTable(arr[index],index);
                PinM.add(index);
                System.out.println("The process loaded successfully to memory");
            }
            else
                System.out.println("No enough space in memory for process P" + index );
            
            arrivingProcesses.remove(0);
            System.out.println();
        }
            
        // output the 5 tables 
        System.out.println("___________________________P0 Table____________________________________");
        System.out.println("   page | frame ");
        for(int i=0; i< process0Table.length; i++) {
            System.out.format("%4s", i);
            System.out.print("    | ");
            System.out.print(process0Table[i]);
            System.out.println();
        } 
        
        System.out.println("___________________________P1 Table____________________________________");
        System.out.println("   page | frame ");
        for(int i=0; i< process1Table.length; i++) {
            System.out.format("%4s", i);
            System.out.print("    | ");
            System.out.print(process1Table[i]);
            System.out.println();
        }
        
        System.out.println("___________________________P2 Table____________________________________");
        System.out.println("   page | frame ");
        for(int i=0; i< process2Table.length; i++) {
            System.out.format("%4s", i);
            System.out.print("    | ");
            System.out.print(process2Table[i]);
            System.out.println();
        }
        
        System.out.println("___________________________P3 Table____________________________________");
        System.out.println("   page | frame ");
        for(int i=0; i< process3Table.length; i++) {
            System.out.format("%4s", i);
            System.out.print("    | ");
            System.out.print(process3Table[i]);
            System.out.println();
        }
        
        System.out.println("___________________________P4 Table____________________________________");
        System.out.println("   page | frame ");
        for(int i=0; i< process4Table.length; i++) {
            System.out.format("%4s", i);
            System.out.print("    | ");
            System.out.print(process4Table[i]);
            System.out.println();
        }
        System.out.println("\n");
        
        //output the content of the memory
        System.out.println("  Physical Memory");
        for (int i = 0; i < memory.length; i++) {
            System.out.format("%5s" , i*pSize);
            System.out.println("  ____________");
            System.out.print("   ");
            System.out.format("%2s", i);
            System.out.print(" |");
            System.out.format(" %7s", memory[i]);
            System.out.print("    |");
            System.out.println();
            System.out.println("      |____________|");
        }
        System.out.println();
        
        //show the processes that have loaded to memory 
        System.out.println("Exist in memory: " + PinM.size() + " processes");
        if(!PinM.isEmpty()) {
            System.out.print("These Processes are: ");
            for(int i=0; i<PinM.size(); i++) {
                System.out.print("P" + PinM.get(i));
                if(i != PinM.size()-1)
                    System.out.print(", ");
            }
            System.out.println(); 
        }
        
        //maping the logical address with the pyisical address
        System.out.print("Enrer the logical address: ");
        Scanner input = new Scanner(System.in);
        int logAddress;
        int pIndex;
        try{
            logAddress = input.nextInt();
            if(logAddress < 0)
                throw new Exception("Logical address can't be negative!");
            System.out.print("Enter from which Process, enter the index 0,1,2.. ");
            pIndex = input.nextInt();
            if(PinM.indexOf(pIndex) == -1) {
                throw new Exception("this process does not exist in memory!");
            }
            
            if(logAddress >= processes[pIndex].getSize())
                throw new Exception("The entered logical address must be less than the process limit!");
            int p = logAddress / pSize;
            int d = logAddress % pSize;
            int f = arr[pIndex][p];
            int phicAddress = f * pSize + d;
            System.out.println("In page: " + p + "  with displacement: " + d);
            System.out.println("The pyisical address is: " + phicAddress);
        }
        catch(Exception e) {
            System.err.println(e.toString());
        } 
        
    } // end of the main function
    
    
    // create the table of the process sent as a parameter
    public static void fillTable(int[] table, int id) {
        Random rand = new Random();
        freeFramesNum = 0;
        ArrayList<Integer> freeFrames = new ArrayList<>();
        for(int i = 0; i< memory.length; i++) {
            if(memory[i].equals("free")) {
                freeFrames.add(i); //store the indexes of free frames
                freeFramesNum++; // count the free frames in memory
            }     
        }

        int diff = freeFramesNum - table.length; // diff is the number of free frames after loading the process
        int i = 0;
        while(diff < freeFramesNum && freeFramesNum > 0) {
            int frame = rand.nextInt(freeFrames.size());
            table[i] = freeFrames.get(frame); // fill the process table with frames indexes
            memory[freeFrames.get(frame)] = "page" + i + "P" + id; // store page in the selected free frame in the memory
            freeFramesNum--;
            i++; // move to next page
            freeFrames.remove(frame);
        }
        
    }
    
    //sort the processes according to their arrival time in acending order using bubble sort Alg.
    public static ArrayList<Process> sortArrival(ArrayList<Process> s){
        if(s.size()>1) {
            Process temp;
            int b = s.size() -1;
            for(int i = 0; i<b; i++) {
                for(int j = 0 ; j < b ; j++){
                    if(s.get(j).getArrivalTime() > s.get(j+1).getArrivalTime()) {
                        temp = s.get(j);
                        s.set(j, s.get(j+1));
                        s.set(j+1, temp);
                    }    
                }
            }
            return s;
        }
       return s;
    }
    
    
}
