import simulator.TRACE;
import simulator.SystemTimer;
import simulator.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;




/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class SimulateRR {
    
    
        public static void main(String[] args){
            
            //simulation 
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int SystemCost;
        String file;
        int Trace;
        int ContextCost;
        int sliceTime;
        
        try{
            
            System.out.println("*** RR Simulator ***");
            System.out.print("Enter configuration file name: ");
            file=reader.readLine();
            System.out.print("Enter slice time: ");
            sliceTime=Integer.parseInt(reader.readLine());
            System.out.print("Enter cost of system call: ");
            SystemCost = Integer.parseInt(reader.readLine());
            System.out.print("Enter cost of context switch: ");
            ContextCost = Integer.parseInt(reader.readLine());
            System.out.print("Enter trace level: ");
            Trace = Integer.parseInt(reader.readLine());
            int trace_copy = TRACE.SET_TRACE_LEVEL(Trace);
            final RoundRobinKernel kernel = new RoundRobinKernel(sliceTime);
            
            Config.init(kernel, ContextCost, SystemCost);

            Config.buildConfiguration(file);
            Config.run();
        
            SystemTimer timer = Config.getSystemTimer();
            System.out.println(timer);
            System.out.println("Context switches: " + Config.getCPU().getContextSwitches());
            System.out.printf("CPU utilization: %.2f\n", (double)timer.getUserTime()/timer.getSystemTime()*100);
            
           
            
            
            
        }
        
        catch(IOException e){
            System.out.println("Enter again");
        }
        
        
        
    }
    
    
    
    
    
}
