import simulator.*;
import simulator.Config;
import simulator.IODevice;
import simulator.Kernel;
import simulator.ProcessControlBlock;
//
import java.io.FileNotFoundException;
import java.io.IOException;
//
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Concrete Kernel type
 * 
 * @author Stephan Jamieson
 * @version 8/3/15
 */
public class FCFSKernel implements Kernel {
    

    private Deque<ProcessControlBlock> readyQueue;
        
    public FCFSKernel() {
		// Set up the ready queue.
              
        readyQueue = new ArrayDeque<>();
    }
    
    private ProcessControlBlock dispatch() {
		// Perform context switch, swapping process
		// currently on CPU with one at front of ready queue.
		// If ready queue empty then CPU goes idle ( holds a null value).
		// Returns process removed from CPU.
                if (!readyQueue.isEmpty()){
                    ProcessControlBlock NewProcess = readyQueue.pop();
                    ProcessControlBlock oldProcess = Config.getCPU().contextSwitch(NewProcess);
                    NewProcess.setState(ProcessControlBlock.State.RUNNING);
                    return oldProcess;
                } else
                    return Config.getCPU().contextSwitch(null);
                
                
	}
            
    
                
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
   
    
    public void interrupt(int interruptType, Object... varargs){
        switch (interruptType) {
            case TIME_OUT:
                throw new IllegalArgumentException("FCFSKernel:interrupt("+interruptType+"...): this kernel does not suppor timeouts.");
            case WAKE_UP:
		ProcessControlBlock currentProcess = (ProcessControlBlock)varargs[1];		// IODevice has finished an IO request for a process.
		currentProcess.setState(ProcessControlBlock.State.READY);		// Retrieve the PCB of the process (varargs[1]), set its state
		readyQueue.add(currentProcess);		// to READY, put it on the end of the ready queue.
		if(Config.getCPU().isIdle()){ this.dispatch();}		// If CPU is idle then dispatch().
                break;
            default:
                throw new IllegalArgumentException("FCFSKernel:interrupt("+interruptType+"...): unknown type.");
        }
    }
    
    private static ProcessControlBlock loadProgram(String filename) {
        return ProcessControlBlockImpl.loadProgram(filename);
    }
}
