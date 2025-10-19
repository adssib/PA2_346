
// Source code for semaphore class:       

class Semaphore   
{
         private int value;
         public Semaphore(int value)
         {
                  this.value = value;
         }
        public Semaphore()
        {
                 this(0);
         }
        public synchronized void Wait()
        {
                  while (this.value <= 0)
                  {
                         try
                        {
                               wait();
                         }
                        catch(InterruptedException e)
                        {
                                 System.out.println ("Semaphore::Wait() - caught InterruptedException: " + e.getMessage() );
                                 e.printStackTrace();
                            }
                    }
                    this.value--;    
           }
           public synchronized void Signal()
           {
                   ++this.value;
                   notify();
           }
           public synchronized void P()
           {
                   this.Wait();
           }
          public synchronized void V()
          {
                   this.Signal();
          }
}

// Source code for character stack:

// Our own exceptions
import CharStackExceptions.*;

class CharStack
{
             /*
            * Some class constants.
             */
             public static final int MIN_SIZE = 7; // Minimum stack size
             public static final int MAX_SIZE = 32; // # of letters in the English alphabet + 6
             public static final int DEFAULT_SIZE = 10; // Default stack size
             /*
             * Class variables
             */
             private static int iSize = DEFAULT_SIZE;
             private static int iTop = 3; // stack[0:9] with four defined values
             private static char aCharStack[] = new char[]  {’a’, ’b’, ’c’, ’d’, ’$’, ’$’,’$’,’$’,’$’,’$’};
            // Default constructor
            public CharStack()
            {
                    // Just do nothing
             }
            // Constructor with supplied size
            public CharStack(int piSize) throws CharStackInvalidSizeException
           {
                   If (piSize < MIN_SIZE || piSize > MAX_SIZE)
                              throw new CharStackInvalidSizeException(piSize);
                   if (piSize != DEFAULT_SIZE)
                  {
                              this.aCharStack = new char[piSize];
                             // Fill in with letters of the alphabet and keep
                             // 6 free blocks
                             for(int i = 0; i < piSize - 6; i++)
                                        this.aCharStack[i] = (char)(’a’ + i);
                        for (int i = 1; i <= 6; i++)
                                        this.aCharStack[piSize - i] = ’$’;
                             this.iTop = piSize - 7;
                             this.iSize = piSize;
                   }
            }
           /*
           * Picks a value from the top without modifying the stack
          */
           public static char pick() throws CharStackEmptyException
           {
                   If (iTop == -1)
                             throw new CharStackEmptyException();
                   return aCharStack[iTop];
            }
           /*
            * Returns arbitrary value from the stack array
           */
           public char getAt(int piPosition) throws CharStackInvalidAceessException
           {
                   if (piPosition < 0 || piPosition >= iSize)
                          throw new CharStackInvalidAceessException();
                   return this.aCharStack[piPosition];
            }
            /*
             * Standard push operation
            */
            public static void push(char pcChar) throws CharStackFullException
            {
                    if (iTop == iSize -1)
                             throw new CharStackFullException();
                    aCharStack[++iTop] = pcChar;
            }
            /*
             * Standard pop operation
           */
            public static char pop() throws CharStackEmptyException
            {
                     if (iTop == -1)
                             throw new CharStackEmptyException();
                    char cChar = aCharStack[iTop];
                    aCharStack[iTop--] = ’$’; // Leave prev. value undefined
                    return cChar;
             }
            /* Getters */
           public int getSize()
          {
                   return this.iSize;
           }
           public int getTop()
          {
                   return this.iTop;
           }
}

// Source code for stack manager:

public class StackManager
{
          // The Stack
           private static CharStack stack = new CharStack();
           private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
           private static final int NUM_PROBERS = 1; // Number of threads dumping stack
           private static int iThreadSteps = 3; // Number of steps they take
          // Semaphore declarations. Insert your code in the following:
          //...
          //...
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
                 public void run()
                 {
                              System.out.println ("Consumer thread [TID=" + this.iTID + "] starts executing.");
                              for (int i = 0; i < StackManager.iThreadSteps; i++)  {
                                       // Insert your code in the following:
                                     // ...
                                     // ...
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
                     public void run()
                     {
                                System.out.println ("Producer thread [TID=" + this.iTID + "] starts executing.");
                                for (int i = 0; i < StackManager.iThreadSteps; i++)  {
                                         // Insert your code in the following:
                                        // ...
                                        //...
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
                     public void run()
                     {
                               System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
                               for (int i = 0; i < 2 * StackManager.iThreadSteps; i++)
                               {
                                        // Insert your code in the following. Note that the stack state must be
                                        // printed in the required format.
                                       // ...
                                       // ...
                               }
                     }
           } // class CharStackProber
} // class StackManager

class BaseThread extends Thread
{
        /*
         * Data members
        */
        public static int iNextTID = 1; // Preserves value across all instances
        protected int iTID;
        public BaseThread()
        {
              this.iTID = iNextTID;
              iNextTID++;
        }
       public BaseThread(int piTID)
       {
             this.iTID = piTID;
       }
}

// Source code for the user defined exception classes:

package CharStackExceptions;

public class CharStackEmptyException extends Exception
{
           public CharStackEmptyException()
           {
                    super ("Char Stack is empty.");
           }
}

public class CharStackFullException extends Exception
{
           public CharStackFullException()
          {
                  super ("Char Stack has reached its capacity of CharStack.MAX_SIZE.");
           }
}

public class CharStackInvalidSizeException extends Exception
{
          public CharStackInvalidSizeException()
          {
                  super("Invalid stack size specified.");
          }
          public CharStackInvalidSizeException (int piStackSize)
          {
                  super ("Invalid stack size specified: " + piStackSize);
           }
}

public class CharStackInvalidAceessException extends Exception
{
           // Fill it up yourself
}


