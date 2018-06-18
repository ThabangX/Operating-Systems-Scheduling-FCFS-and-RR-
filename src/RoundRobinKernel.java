
import simulator.Kernel;
import simulator.ProcessControlBlock;
import simulator.Config;
import simulator.IODevice;
import java.util.ArrayDeque;
import static simulator.SystemCall.IO_REQUEST;
import static simulator.SystemCall.MAKE_DEVICE;
import static simulator.SystemCall.TERMINATE_PROCESS;
import java.util.Deque;
import simulator.InterruptHandler;
import static simulator.InterruptHandler.TIME_OUT;
import static simulator.InterruptHandler.WAKE_UP;
import static simulator.SystemCall.EXECVE;




/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class RoundRobinKernel implements Kernel {
    
    
    private final Deque<ProcessControlBlock> readyQueue;
    private static int timeSlice;
    
    
    public RoundRobinKernel(int timeslice){
          
        readyQueue = new ArrayDeque<>();
        timeSlice = timeslice;
      
    }
    
        private ProcessControlBlock dispatch() {
      
      ProcessControlBlock   ProcessOutgoing;
      if(!readyQueue.isEmpty())
      {
         ProcessOutgoing=Config.getCPU().contextSwitch(readyQueue.pollFirst());
         Config.getSystemTimer().scheduleInterrupt(timeSlice,this,Config.getCPU().getCurrentProcess().getPID());
         Config.getCPU().getCurrentProcess().setState(ProcessControlBlock.State.RUNNING);
      }
      else
      {  ProcessOutgoing=Config.getCPU().contextSwitch(null);} 
        
      return ProcessOutgoing; }
        
        
          /**   
             public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
             case MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);
                    Config.addDevice(device);
                }
                break;
             case EXECVE: 
                {
                    ProcessControlBlock pcb = this.loadProgram((String)varargs[0]);
                    if (pcb!=null) {
                        // Loaded successfully.
						// Now add to end of ready queue.
						// If CPU idle then call dispatch.
                            if(pcb!=null){
                                readyQueue.add(pcb);
                                if(Config.getCPU().isIdle()){
                                    this.dispatch();
                                    pcb.setState(ProcessControlBlock.State.RUNNING);
                                }
                            }        
                    }
                    else {
                        result = -1;
                    }
                }
                break;
             case IO_REQUEST: 
                {
		ProcessControlBlock currentProcess = Config.getCPU().getCurrentProcess();			// IO request has come from process currently on the CPU.
					                                                                // Get PCB from CPU.
		IODevice currentDevice = Config.getDevice((Integer)varargs[0]);			// Find IODevice with given ID: Config.getDevice((Integer)varargs[0]);
		currentDevice.requestIO((Integer)varargs[1], currentProcess, this);			// Make IO request on device providing burst time (varages[1]),
				                                                             	// the PCB of the requesting process, and a reference to this kernel (so // that the IODevice can call interrupt() when the request is completed.
					                                                        //
		currentProcess.setState(ProcessControlBlock.State.WAITING);			// Set the PCB state of the requesting process to WAITING.
		this.dispatch();			                                     // Call dispatch().
                }
                break;
             case TERMINATE_PROCESS:
                {
		  ProcessControlBlock currentProcess = Config.getCPU().getCurrentProcess();			// Process on the CPU has terminated.
		  currentProcess.setState(ProcessControlBlock.State.TERMINATED);			// Get PCB from CPU.
   		  this.dispatch();			// Set status to TERMINATED.
                    // Call dispatch().
                }
                break;
             default:
                result = -1;
        }
        return result;
    }
           * 
           * public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
             case MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);   // Loaded successfully.
						// Now add to end of ready queue.
						// If CPU idle then call dispatch.
                    Config.addDevice(device);
                }
                break;
             case EXECVE: 
                {
                    ProcessControlBlock Nowprocess = loadProgram((String)varargs[0]);
                    if (Nowprocess!=null) {
                        
                        readyQueue.add(Nowprocess); //add process 
			if(Config.getCPU().isIdle()){ //if it's idle then launch process
                            this.dispatch(); // call dispatch
                            //set to Running
                            Nowprocess.setState(ProcessControlBlock.State.RUNNING);
                            
                          }
                    }
                    else {
                        result = -1;
                    }
                    
                }
                break;
             case IO_REQUEST: 
                {
                    //get current process
                    ProcessControlBlock currentProcess = Config.getCPU().getCurrentProcess();
                    //select type of devicebt id
                    IODevice DeviceCurrent = Config.getDevice((Integer)varargs[0]);
                    
                    DeviceCurrent.requestIO((Integer)varargs[1], currentProcess, this);
                    //wait for IO REQuest
                    
                    currentProcess.setState(ProcessControlBlock.State.WAITING);
                    
                    //get the time for wait
                    Config.getSystemTimer().cancelInterrupt(currentProcess.getPID());
                    this.dispatch();
                    
                }
                break;
             case TERMINATE_PROCESS:
                {
		    ProcessControlBlock currentProcess = Config.getCPU().getCurrentProcess();
                    //Terminated
                    currentProcess.setState(ProcessControlBlock.State.TERMINATED);
                    Config.getSystemTimer().cancelInterrupt(currentProcess.getPID());
                    //call dispatch
                    this.dispatch();
                    
                }
                break;
             default:
                result = -1;
        }
        return result;
    }**/
 
    
                
    
    @Override
    public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
             case MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);
                    Config.addDevice(device);
                }
                break;
             case EXECVE: 
                {
                    ProcessControlBlock BLOCK = loadProgram((String)varargs[0]);
                    BLOCK.setPriority((Integer)varargs[1]);
                    if (BLOCK!=null) {
                        readyQueue.add(BLOCK);
                        if(Config.getCPU().isIdle()){dispatch();}
                    }
                    else { result = -1;}
                }
                break;
                
             case IO_REQUEST: 
                {
                  ProcessControlBlock BLOCK=Config.getCPU().getCurrentProcess();
                  IODevice Device=Config.getDevice((Integer)varargs[0]);
		  Device.requestIO((Integer)varargs[1],BLOCK,this);
		  Config.getSystemTimer().cancelInterrupt(BLOCK.getPID());
                  BLOCK.setState(ProcessControlBlock.State.WAITING);
                  dispatch();
                }
                break;
                
             case TERMINATE_PROCESS:
             {
                  ProcessControlBlock BLOCK=Config.getCPU().getCurrentProcess();
                  Config.getSystemTimer().cancelInterrupt(BLOCK.getPID());
                  BLOCK.setState(ProcessControlBlock.State.TERMINATED);
                  dispatch();
                }
                break;
             default:
                result = -1;
        }
        return result;
    }

   
    public void interrupt(int interruptType, Object... varargs){
        switch (interruptType) {
            case TIME_OUT:
            {
                ProcessControlBlock BLOCKX=Config.getCPU().getCurrentProcess();
                
                
                readyQueue.add(BLOCKX);
                if(readyQueue.peekFirst().equals(BLOCKX)){dispatch(); }
                
                
                else{BLOCKX.setState(ProcessControlBlock.State.READY);
                  dispatch();
                }
           
                break;
            }
               
            case WAKE_UP:
            {
            
				  
               ProcessControlBlock WokeBlock=((ProcessControlBlock)varargs[1]);
               WokeBlock.setState(ProcessControlBlock.State.READY);
               readyQueue.add(WokeBlock);
               if(Config.getCPU().isIdle()){dispatch();}
               break;
            }
            default:
            {
                throw new IllegalArgumentException("RRKernel:interrupt("+interruptType+"...): unknown type.");
        }
        }
    }
    

        
        
        
        
        
        
            private static ProcessControlBlock loadProgram(String filename) {
        return ProcessControlBlockImpl.loadProgram(filename);
    }
    
}
