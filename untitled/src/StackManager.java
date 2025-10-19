import CharStackExceptions.CharStackEmptyException;
import CharStackExceptions.CharStackFullException;
import CharStackExceptions.CharStackInvalidAceessException;

public class StackManager
{
    // The Stack
    private static CharStack stack = new CharStack();
    private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
    private static final int NUM_PROBERS = 1; // Number of threads dumping stack
    private static int iThreadSteps = 3; // Number of steps they take

    // Semaphore declaration for Task 2 - Only need mutex for mutual exclusion
    private static Semaphore mutex = new Semaphore(1);

    // ...
    // The main()
    public static void main(String[] argv)
    {
        // Some initial stats...
        try
        {
            System.out.println("Main thread starts executing.");
            System.out.println("Initial value of top = " + stack.getTop() + ".");
            System.out.println("Initial value of stack top = " + stack.pick() + ".");
            System.out.println("Main thread will now fork several threads.");
        }
        catch(CharStackEmptyException e)
        {
            System.out.println("Caught exception: StackCharEmptyException");
            System.out.println("Message : " + e.getMessage());
            System.out.println("Stack Trace : ");
            e.printStackTrace();
        }
        /*
         * The birth of threads
         */
        Consumer ab1 = new Consumer();
        Consumer ab2 = new Consumer();
        System.out.println("Two Consumer threads have been created.");
        Producer rb1 = new Producer();
        Producer rb2 = new Producer();
        System.out.println("Two Producer threads have been created.");
        CharStackProber csp = new CharStackProber();
        System.out.println("One CharStackProber thread has been created.");
        /*
         * start executing
         */
        ab1.start();
        rb1.start();
        ab2.start();
        rb2.start();
        csp.start();
        /*
         * Wait by here for all forked threads to die
         */
        try
        {
            ab1.join();
            ab2.join();
            rb1.join();
            rb2.join();
            csp.join();
            // Some final stats after all the child threads terminated...
            System.out.println("System terminates normally.");
            System.out.println("Final value of top = " + stack.getTop() + ".");
            System.out.println("Final value of stack top = " + stack.pick() + ".");
            System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
//            System.out.println("Stack access count = " + stack.getAccessCounter());
        }
        catch(InterruptedException e)
        {
            System.out.println("Caught InterruptedException: " + e.getMessage());
            System.exit(1);
        }
        catch(Exception e)
        {
            System.out.println("Caught exception: " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Stack Trace : ");
            e.printStackTrace();
        }
    } // main()`


    static class Consumer extends BaseThread {
        private char copy; // A copy of a block returned by pop()
        // Inside Consumer.run()
        public void run()
        {
            System.out.println ("Consumer thread [TID=" + this.iTID + "] starts executing.");
            for (int i = 0; i < StackManager.iThreadSteps; i++)  {

                try {
                    // Acquire lock
                    mutex.P();

                    // Critical section: pop from stack
                    copy = stack.pop();

                    // Release lock
                    mutex.V();

                    // Print outside critical section
                    System.out.println("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy);
                } catch (CharStackEmptyException e) {
                    mutex.V(); // Make sure to release lock even on exception
                    System.out.println("Consumer [TID=" + this.iTID + "]: Stack is empty");
                }
            }

            System.out.println("Consumer thread [TID=" + this.iTID + "] terminates.");
        }
    } // class Consumer

    /*
     * Inner class Producer
     */
    static class Producer extends BaseThread
    {
        private char block;

        public void run()
        {
            System.out.println ("Producer thread [TID=" + this.iTID + "] starts executing.");
            for (int i = 0; i < StackManager.iThreadSteps; i++)
            {
                try
                {
                    mutex.P();
                    char top = stack.pick();
                    block = (char)(top + 1);
                    stack.push(block);
                    mutex.V();

                    System.out.println("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block);
                }
                catch(CharStackFullException e)
                {
                    mutex.V();
                    System.out.println("Producer [TID=" + this.iTID + "]: Stack is full");
                }
                catch(CharStackEmptyException e)
                {
                    mutex.V();
                    System.out.println("Producer [TID=" + this.iTID + "]: Stack is empty");
                }
            }

            System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
        }
    }

    /*
     * Inner class CharStackProber to dump stack contents
     */
    static class CharStackProber extends BaseThread {
        public void run() {
            System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");

            for (int i = 0; i < 2 * StackManager.iThreadSteps; i++) {
                // Acquire lock
                mutex.P();

                // Critical section: read and print stack
                StringBuilder output = new StringBuilder("Stack S = (");

                for (int j = 0; j < stack.getSize(); j++) {
                    try {
                        char c = stack.getAt(j);
                        output.append("[").append(c).append("]");

                        if (j < stack.getSize() - 1) {
                            output.append(",");
                        }
                    } catch (CharStackInvalidAceessException e) {
                        System.out.println("CharStackProber [TID=" + this.iTID + "]: Invalid access");
                    }
                }

                output.append(")");
                System.out.println(output.toString());

                // Release lock
                mutex.V();
            }

            System.out.println("CharStackProber thread [TID=" + this.iTID + "] terminates.");
        }
    } // class CharStackProber
}// class StackManager