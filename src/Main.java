import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GumballMachine gumballMachine = new GumballMachine();

        boolean end = false;
        while (!end) {
            System.out.println("----------------");
            System.out.println("[1] Add Gumballs");
            System.out.println("[2] Insert Quarter");
            System.out.println("[3] Remove Quarter");
            System.out.println("[4] Turn Handle");
            System.out.println("[5] Quit");
            System.out.println("----------------");
            System.out.print("Choose an action: ");
            Scanner input = new Scanner(System.in);
            int option = input.nextInt();
            System.out.println("\n----------------");

            switch (option) {
                case 1:
                    System.out.print("How many gumballs would you like to add? ");
                    int newGumballs = input.nextInt();
                    System.out.println();
                    if(newGumballs > 0){
                        gumballMachine.addGumballs(newGumballs);
                    } else {
                        System.out.print("You must enter a positive number of gumballs\n");
                    }
                    break;
                case 2:
                    gumballMachine.insertUSQuarter();
                    break;
                case 3:
                    gumballMachine.removeUSQuarter();
                    break;
                case 4:
                    gumballMachine.turnHandle();
                    break;
                case 5:
                    end = true;
                    break;
            }
        }
    }
}

class GumballMachine {
    private State currentState;

    private double balance;
    private boolean hasUSQuarter;
    private int gumballCount;

    public GumballMachine() {
        this.currentState = new EmptyState();
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void addGumballs(int count) {
        currentState.addGumballs(this, count);
    }

    public void insertUSQuarter() {
        currentState.insertUSQuarter(this);
    }

    public void removeUSQuarter() {
        currentState.removeUSQuarter(this);
    }

    public void turnHandle() {
        currentState.turnHandle(this);
    }

    public void increaseGumballCount(int count) {
        this.gumballCount += count;
        System.out.printf("Gumballs added: %d.\n %d gumballs in machine.\n", count, this.gumballCount);
    }

    public void toggleQuarter(){
        this.hasUSQuarter = !this.hasUSQuarter;
        System.out.printf("Quarter %s, machine %shas a quarter in it\n", this.hasUSQuarter? "added":"removed", this.hasUSQuarter? "":"no longer ");
    }

    public int getCount() {
        return this.gumballCount;
    }

    public void dispenseGumball() {
        this.gumballCount--;
        this.balance += 0.25;
        this.hasUSQuarter = false;
        System.out.printf("There are %d gumballs remaining.\n The machine's balance is $%f\n", this.gumballCount, this.balance);
    }
}

interface State {

    void addGumballs(GumballMachine gumballMachine, int count);

    void removeUSQuarter(GumballMachine gumballMachine);

    void insertUSQuarter(GumballMachine gumballMachine);

    void turnHandle(GumballMachine gumballMachine);
}

// State 1 - Empty: NoGumballs / NoQuarterInSlot
class EmptyState implements State {

    @Override
    public void addGumballs(GumballMachine gumballMachine, int count) {
        gumballMachine.increaseGumballCount(count);
        if (gumballMachine.getCount() > 0) {
            gumballMachine.setCurrentState(new FilledState());
        }
    }

    @Override
    public void removeUSQuarter(GumballMachine gumballMachine) {
        System.out.println("There is not a quarter in the slot to remove!");
    }

    @Override
    public void insertUSQuarter(GumballMachine gumballMachine) {
        gumballMachine.toggleQuarter();
        gumballMachine.setCurrentState(new QuarterEmptyState());
    }

    @Override
    public void turnHandle(GumballMachine gumballMachine) {
        System.out.println("You attempt to turn the handle, but cannot because there is no quarter.\n" +
                "You could place a quarter in the slot, but it would do you no good without gumballs.\n" +
                "Please add gumballs to the machine.\n");
    }
}

// State 2 - QuarterEmpty: NoGumballs / QuarterInSlot
class QuarterEmptyState implements State {

    @Override
    public void addGumballs(GumballMachine gumballMachine, int count) {
        gumballMachine.increaseGumballCount(count);
        if (gumballMachine.getCount() > 0) {
            gumballMachine.setCurrentState(new FilledQuarterState());
        }
    }

    @Override
    public void removeUSQuarter(GumballMachine gumballMachine) {
        gumballMachine.toggleQuarter();
        gumballMachine.setCurrentState(new EmptyState());
    }

    @Override
    public void insertUSQuarter(GumballMachine gumballMachine) {
        System.out.println("There is already a quarter in the slot!");
    }

    @Override
    public void turnHandle(GumballMachine gumballMachine) {
        System.out.println("You attempt to turn the handle, but cannot because there are no gumballs left.\n" +
                "Please add gumballs to the machine.\n");
    }
}

// State 3 - Filled: Gumballs / NoQuarterInSlot
class FilledState implements State {

    @Override
    public void addGumballs(GumballMachine gumballMachine, int count) {
        gumballMachine.increaseGumballCount(count);
    }

    @Override
    public void removeUSQuarter(GumballMachine gumballMachine) {
        System.out.println("There is not a quarter in the slot to remove!");
    }

    @Override
    public void insertUSQuarter(GumballMachine gumballMachine) {
        gumballMachine.toggleQuarter();
        gumballMachine.setCurrentState(new FilledQuarterState());
    }

    @Override
    public void turnHandle(GumballMachine gumballMachine) {
        System.out.println("You attempt to turn the handle, but cannot because there is no quarter.\n" +
                "Please place a quarter in the slot.\n");
    }
}

// State 4 - FilledQuarter: Gumballs / QuarterInSlot
class FilledQuarterState implements State {

    @Override
    public void addGumballs(GumballMachine gumballMachine, int count) {
        gumballMachine.increaseGumballCount(count);
        gumballMachine.setCurrentState(new FilledState());
    }

    @Override
    public void removeUSQuarter(GumballMachine gumballMachine) {
        gumballMachine.toggleQuarter();
        gumballMachine.setCurrentState(new FilledState());
    }

    @Override
    public void insertUSQuarter(GumballMachine gumballMachine) {
        System.out.println("There is already a quarter in the slot!");
    }

    @Override
    public void turnHandle(GumballMachine gumballMachine) {
        System.out.println("You turn the handle and a gumball comes out. Enjoy!\n");
        gumballMachine.dispenseGumball();
        gumballMachine.setCurrentState(gumballMachine.getCount() == 0 ? new EmptyState() : new FilledState());
    }
}