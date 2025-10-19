import CharStackExceptions.*;

public class StackManager
{
    // The Stack
    private static CharStack stack = new CharStack();
    private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
    private static final int NUM_PROBERS = 1; // Number of threads dumping stack
    private static int iThreadSteps = 3; // Number of steps they take
    // Semaphore declarations. Insert your code in the following:
    // Inside StackManager class
    private static final int STACK_SIZE = 10;
    private static final int INITIAL_OCCUPIED_SLOTS = CharStack.getTop() + 1; // 3 + 1 = 4

    // Semaphore declarations. Insert your code in the following:
    private static Semaphore sMutex = new Semaphore(1);
    private static Semaphore sFull = new Semaphore(INITIAL_OCCUPIED_SLOTS);
    private static Semaphore sEmpty = new Semaphore(STACK_SIZE - INITIAL_OCCUPIED_SLOTS);
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
        System.out.println ("Two Consumer threads have been created.");
        Producer rb1 = new Producer();
        Producer rb2 = new Producer();
        System.out.println ("Two Producer threads have been created.");
        CharStackProber csp = new CharStackProber();
        System.out.println ("One CharStackProber thread has been created.");
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
            System.out.println("Stack access count = " + stack.getAccessCounter());
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
    } // main()


    /*
     * Inner Consumer thread class
     */
    static class Consumer extends BaseThread
    {
        private char copy; // A copy of a block returned by pop()
        // Inside Consumer.run()
        public void run()
        {
            System.out.println ("Consumer thread [TID=" + this.iTID + "] starts executing.");
            for (int i = 0; i < StackManager.iThreadSteps; i++)  {

                try {
                    // 1. Wait for a full slot (Bounded Buffer)
                    StackManager.sFull.P();

                    // 2. Acquire mutual exclusion lock (Critical Section Entry)
                    StackManager.sMutex.P();

                    // --- CRITICAL SECTION START ---
                    this.copy = CharStack.pop();
                    // --- CRITICAL SECTION END ---

                } catch(CharStackEmptyException e) {
                    // Should not happen if sFull is managed correctly
                    System.out.println("Consumer " + this.iTID + " caught: " + e.getMessage());
                    StackManager.sFull.V(); // Restore sFull token
                } catch(Exception e) {
                    System.out.println("Consumer " + this.iTID + " caught: " + e.getMessage());
                } finally {
                    // 3. Release mutual exclusion lock (Critical Section Exit)
                    StackManager.sMutex.V();

                    // 4. Signal that a slot is now empty (Bounded Buffer)
                    StackManager.sEmpty.V();
                }

                System.out.println("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy);
            }
            System.out.println ("Consumer thread [TID=" + this.iTID + "] terminates.");
        }
    } // class Consumer


    /*
     * Inner class Producer
     */
    static class Producer extends BaseThread
    {
        private char block; // block to be returned
        // Inside Producer.run()
        public void run()
        {
            System.out.println ("Producer thread [TID=" + this.iTID + "] starts executing.");
            for (int i = 0; i < StackManager.iThreadSteps; i++)  {

                char nextChar = ' ';
                try {
                    // 1. Wait for an empty slot (Bounded Buffer)
                    StackManager.sEmpty.P();

                    // 2. Acquire mutual exclusion lock (Critical Section Entry)
                    StackManager.sMutex.P();

                    // --- CRITICAL SECTION START ---
                    char topChar = CharStack.pick();
                    nextChar = (char) (topChar + 1);
                    CharStack.push(nextChar);
                    this.block = nextChar;
                    // --- CRITICAL SECTION END ---

                } catch(CharStackFullException e) {
                    // Should not happen if sEmpty is managed correctly
                    System.out.println("Producer " + this.iTID + " caught: " + e.getMessage());
                    StackManager.sEmpty.V(); // Restore sEmpty token
                } catch(Exception e) {
                    System.out.println("Producer " + this.iTID + " caught: " + e.getMessage());
                } finally {
                    // 3. Release mutual exclusion lock (Critical Section Exit)
                    StackManager.sMutex.V();

                    // 4. Signal that a slot is now full (Bounded Buffer)
                    StackManager.sFull.V();
                }

                System.out.println("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block);
            }
            System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
        }
    } // class Producer


    /*
     * Inner class CharStackProber to dump stack contents
     */
    static class CharStackProber extends BaseThread
    {
        // Inside CharStackProber.run()
        public void run()
        {
            System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
            for (int i = 0; i < 2 * StackManager.iThreadSteps; i++)
            {

                // 1. Acquire mutual exclusion lock (Critical Section Entry)
                StackManager.sMutex.P();

                // --- CRITICAL SECTION START ---
                // Code to read and print the stack state (already provided in A2source.java)
                StringBuilder sb = new StringBuilder("Stack S= (");
                int size = CharStack.getSize();

                for (int j = 0; j < size; j++) {
                    char c = '$';
                    try {
                        c = StackManager.stack.getAt(j);
                    } catch (CharStackExceptions.CharStackInvalidAceessException e) {
                        // Should not happen
                    }
                    sb.append("[").append(c).append("]");
                    if (j < size - 1) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                System.out.println(sb.toString());
                // --- CRITICAL SECTION END ---

                // 2. Release mutual exclusion lock (Critical Section Exit)
                StackManager.sMutex.V();
            }
            System.out.println("CharStackProber thread [TID=" + this.iTID + "] terminates.");
        }
    } // class CharStackProber
} // class StackManagerBa