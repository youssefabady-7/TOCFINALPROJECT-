# NFA to DFA Visual Simulator

This is a separate Java console project for manually entering an NFA and converting it to a DFA.

## Features

- User manually enters NFA states.
- User enters alphabet symbols.
- User enters start state and final states.
- User enters NFA transitions, including epsilon transitions.
- Converts NFA -> DFA using Subset Construction.
- Draws readable NFA and DFA transition tables.
- Tests input strings on the generated DFA.
- Includes a professor demo NFA from the menu.

## Project structure

```text
NFAToDFAVisualSimulator/
  README.md
  TEST_CASES.txt
  NFAToDFAVisualSimulator_Explanation.pdf
  src/
    Main.java
    UI.java
    State.java
    NFA.java
    DFAState.java
    DFA.java
    TransitionTablePrinter.java
    SubsetConstruction.java
    StringTester.java
```

## How input works

State list example:

```text
q0,q1,q2
```

Alphabet example:

```text
a,b
```

Transition examples:

```text
q0 a q0,q1
q0 eps q2
q1 b q2
```

Type `done` when you finish entering transitions.

## Menu options

```text
1. Enter a new NFA
2. Display NFA transition table
3. Convert NFA to DFA
4. Display DFA transition table
5. Test input string on DFA
6. Load professor demo NFA
0. Exit
```

The professor demo NFA recognizes all strings over `{a,b}` that end with `ab`.
It prints the NFA table, converts it to a DFA, prints the DFA table, and tests accepted and rejected examples. After running the demo, option `5` still lets you test your own strings.

The same demo is also documented in `TEST_CASES.txt`.

## Class explanation

- `Main`: Starts the simulator.
- `UI`: Handles all console menus and user input.
- `State`: Represents one NFA state.
- `NFA`: Stores manually entered NFA data.
- `DFAState`: Represents a DFA state as a set of NFA states.
- `DFA`: Stores the converted DFA.
- `TransitionTablePrinter`: Prints clean NFA and DFA transition tables.
- `SubsetConstruction`: Converts NFA to DFA.
- `StringTester`: Tests strings against the DFA.

## How to run from the command line

```powershell
cd NFAToDFAVisualSimulator
javac -d out src\*.java
java -cp out Main
```

## How to run in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Choose `File -> Open`.
3. Select the `NFAToDFAVisualSimulator` folder.
4. Mark the `src` folder as Sources Root if needed.
5. Open `src/Main.java`.
6. Click Run.

## How to run in NetBeans

1. Create a new Java project.
2. Copy all files from `NFAToDFAVisualSimulator/src` into the project's `src` folder.
3. Set `Main` as the main class.
4. Run the project.
