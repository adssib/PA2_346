
import CharStackExceptions.*;

public class StackManager_Task4
{
    private static CharStack stack = new CharStack();
    private static final int NUM_PROBERS = 1;
    private static int iThreadSteps = 3;

    // Semaphores
    private static Semaphore mutex = new Semaphore(1);
    private static int producersFinished = 0;
    private static Semaphore producersMutex = new Semaphore(1);
    private static Semaphore consumersBlock = new Semaphore(0);

    public static void main(String[] argv)
    {
        try {
            System.out.println("Main thread starts executing.");
            System.out.println("Initial value of top = " + stack.getTop() + ".");
            System.out.println("Initial value of stack top = " + stack.pick() + ".");
            System.out.println("Main thread will now fork several threads.");
        } catch (CharStackEmptyException e) {
            e.printStackTrace();
        }

        // Thread creation
        Consumer ab1 = new Consumer();
        Consumer ab2 = new Consumer();
        System.out.println("Two Consumer threads have been created.");
        Producer rb1 = new Producer();
        Producer rb2 = new Producer();
        System.out.println("Two Producer threads have been created.");
        CharStackProber csp = new CharStackProber();
        System.out.println("One CharStackProber thread has been created.");

        // Start threads
        ab1.start();
        ab2.start();
        rb1.start();
        rb2.start();
        csp.start();

        // Join threads
        try {
            ab1.join();
            ab2.join();
            rb1.join();
            rb2.join();
            csp.join();

            System.out.println("System terminates normally.");
            System.out.println("Final value of top = " + stack.getTop() + ".");
            System.out.println("Final value of stack top = " + stack.pick() + ".");
            System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Consumer extends BaseThread {
        private char copy;

        public void run() {
            System.out.println("Consumer thread [TID=" + this.iTID + "] starts executing.");

            try {
                // Wait until producers finish
                consumersBlock.P();
            } catch (Exception e) {}

            for (int i = 0; i < StackManager_Task4.iThreadSteps; i++) {
                try {
                    mutex.P();
                    copy = stack.pop();
                    mutex.V();

                    System.out.println("Consumer thread [TID=" + this.iTID + "] pops character = " + this.copy);
                } catch (CharStackEmptyException e) {
                    mutex.V();
                    System.out.println("Consumer [TID=" + this.iTID + "]: Stack is empty");
                }
            }
            System.out.println("Consumer thread [TID=" + this.iTID + "] terminates.");
        }
    }

    static class Producer extends BaseThread {
        private char block;

        public void run() {
            System.out.println("Producer thread [TID=" + this.iTID + "] starts executing.");
            for (int i = 0; i < StackManager_Task4.iThreadSteps; i++) {
                try {
                    mutex.P();
                    char top = stack.pick();
                    block = (char)(top + 1);
                    stack.push(block);
                    mutex.V();

                    System.out.println("Producer thread [TID=" + this.iTID + "] pushes character = " + this.block);
                } catch (CharStackFullException | CharStackEmptyException e) {
                    mutex.V();
                    System.out.println("Producer [TID=" + this.iTID + "]: Stack error");
                }
            }

            // Once finished all pushes, increment counter
            producersMutex.P();
            producersFinished++;
            if (producersFinished == 2) {
                // Both producers finished — allow consumers to start
                consumersBlock.V();
                consumersBlock.V();
            }
            producersMutex.V();

            System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
        }
    }

    static class CharStackProber extends BaseThread {
        public void run() {
            System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");

            for (int i = 0; i < 2 * StackManager_Task4.iThreadSteps; i++) {
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

}