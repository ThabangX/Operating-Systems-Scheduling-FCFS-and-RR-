
import simulator.ProcessControlBlock;
import simulator.ProcessControlBlock.State;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import simulator.CPUInstruction;
import simulator.IOInstruction;
import simulator.Instruction;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class ProcessControlBlockImpl implements ProcessControlBlock{
    
    private String programName;
    private final int pid;
    private static int PC = 1;
    private List<Instruction> instructionList;
    private int priority;
    private State state;
   
    
    public ProcessControlBlockImpl(){
        instructionList = new ArrayList<>();
        pid = PC++;
    }
    
    
        
    @Override
    public int getPID() {
        return pid;
    }

    
    @Override
    public String getProgramName() {
        return programName;
    }

    @Override
    public int getPriority() {
        return priority;
    }
    
    @Override
    public int setPriority(int value) {
        int temp = priority;
        priority = value;
        return temp;
    }
    
    @Override
    public Instruction getInstruction() {
        return instructionList.get(0);
    }

    @Override
    public boolean hasNextInstruction() {
       return (instructionList.size()>1);
    }

    @Override
    public void nextInstruction() {
        instructionList.remove(0);
    }
    
    public void setInstructionList(List<Instruction> il) {
        instructionList = il;
    }

    
    @Override
    public State getState() {
        return state;
    }
    @Override
    public void setState(State state) {
        this.state = state;
    }
    
    public String toString(){
        return String.format("process(pid=%d, state=%s, name=\"%s\")", this.getPID(), this.getState(), this.getProgramName());
    }
    
    public static ProcessControlBlock loadProgram(String filename){
        
        ProcessControlBlockImpl process = new ProcessControlBlockImpl();
        
     try {
            final BufferedReader lineReader = new BufferedReader(new FileReader(filename));
            String inLine = lineReader.readLine();
            while (inLine!=null) {
                
                inLine.trim();
                if (inLine.startsWith("#") || inLine.equals("")) {
                   
                }
                else if (inLine.startsWith("CPU")) {
                    Scanner inScanner = new Scanner(inLine);
                    inScanner.next(); 
                    if (!inScanner.hasNextInt()) {
                        System.out.println("CPU burst time missing: \""+inLine+"\".");
                        System.exit(-1);
                    }
                    final int BurstTime = inScanner.nextInt();
                    CPUInstruction CPUInstruction = new CPUInstruction(BurstTime);
                    process.instructionList.add(CPUInstruction);
                }
                else if (inLine.startsWith("IO")) {
                    Scanner scanner = new Scanner(inLine);
                    scanner.next();
                    if (!scanner.hasNextInt()) {
                        System.out.println("IO burst time missing: \""+inLine+"\".");
                        System.exit(-1);
                    }
                    
                    final int ioBurstTime = scanner.nextInt();
                    if (!scanner.hasNextInt()) {
                        System.out.println("IO missing device type: \""+inLine+"\".");
                        System.exit(-1);
                    }
                    final int ioID = scanner.nextInt();
                    IOInstruction ioInst = new IOInstruction(ioBurstTime,ioID);
                    process.instructionList.add(ioInst);
                }
                else {
                    System.out.println("Unrecognised token in configuration file : \""+filename+"\".");
                    lineReader.close();
                    System.exit(-1);
                }
                inLine = lineReader.readLine();
            }
            lineReader.close();
            process.state = State.READY;
            process.programName = filename;
            return process;
        }
        catch (FileNotFoundException e) {
            System.out.println("File \""+filename+"\" not found.");
            System.exit(-1);
        }
        catch (IOException i) {
            System.out.println("IO Error reading from \""+filename+"\".");
            //<editor-fold defaultstate="collapsed" desc="comment">
            System.exit(-1);
//</editor-fold>
        }  
        return process;
        
        
        
    }
    
    
    
    
    
    
    
}
