# Regular Expression to DFA Conversion

This is a plain Java university project for Formal Languages and Automata Theory.

## Features

- Reads a regular expression from the user.
- Validates the regular expression syntax.
- Converts Regular Expression -> NFA using Thompson's Construction.
- Converts NFA -> DFA using Subset Construction.
- Minimizes the DFA.
- Displays states, start state, final states, and transition tables.
- Tests input strings against the minimized DFA.
- Includes a professor demo test case from the menu.

## Supported regular expression syntax

- `a`, `b`, `0`, `1`, etc. are input symbols.
- `|` means union.
- `*` means zero or more.
- `+` means one or more.
- `?` means optional.
- Parentheses `(` and `)` are supported.
- Concatenation is implicit: `ab` means `a` followed by `b`.
- Use `#` for epsilon.

Examples:

- `a|b`
- `ab*`
- `(a|b)*abb`
- `a(b|c)+`
- `#|ab`

## Menu options

```text
1. Enter regular expression and convert
2. Display NFA
3. Display DFA
4. Display minimized DFA
5. Test input string using minimized DFA
6. Run professor demo test case
0. Exit
```

The professor demo uses this regular expression:

```text
(a|b)*abb
```

It represents all strings over `{a,b}` that end with `abb`. The demo prints the minimized DFA and tests accepted and rejected example strings. After running the demo, option `5` still lets you test your own strings.

The same demo is also documented in `TEST_CASES.txt`.

## Project structure

```text
RegexToDFAProject/
  README.md
  TEST_CASES.txt
  RegexToDFAProject_Explanation.pdf
  src/
    Main.java
    UI.java
    RegexParser.java
    State.java
    NFA.java
    DFAState.java
    DFA.java
    ThompsonConstruction.java
    SubsetConstruction.java
    StateSetComparator.java
    DFAMinimizer.java
    StringTester.java
```

## Class explanation

- `Main`: Starts the application.
- `UI`: Provides the console menu, handles user input, and prints results.
- `RegexParser`: Validates the regex, inserts explicit concatenation internally, and converts infix regex to postfix.
- `State`: Represents an NFA state and its transitions.
- `NFA`: Stores NFA states, alphabet, start state, final states, and display logic.
- `DFAState`: Represents a DFA state, which may correspond to a set of NFA states.
- `DFA`: Stores DFA states and prints a readable transition table.
- `ThompsonConstruction`: Builds an NFA from postfix regex.
- `SubsetConstruction`: Converts the NFA to a DFA using epsilon-closure and move operations.
- `StateSetComparator`: Helps store sets of NFA states in sorted maps.
- `DFAMinimizer`: Minimizes the DFA by partition refinement.
- `StringTester`: Runs input strings through the minimized DFA.

## How to run from the command line

```powershell
cd RegexToDFAProject
javac -d out src\*.java
java -cp out Main
```

## How to run in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Choose `File -> Open`.
3. Select the `RegexToDFAProject` folder.
4. Mark the `src` folder as Sources Root if IntelliJ does not do it automatically.
5. Open `src/Main.java`.
6. Click Run.

## How to run in NetBeans

1. Create a new Java project.
2. Copy all files from `RegexToDFAProject/src` into the project's `src` folder.
3. Set `Main` as the main class.
4. Run the project.
