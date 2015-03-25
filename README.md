# PBasic

**Pbasic** is a simple programming language which compiles to Prof Nux's **Pidgen** assembly.

###Sample PBasic program

```
INT fib_ret;
INT fib_in;

SUB fib;

    REM store local parameter;
    INT in;
    SET in = fib_in;

    IF (< in 2);
        SET fib_ret = 1;
        RETURN;
    FI;

    INT res;

    REM recurse down;

    SET fib_in = (- in 1);
    GOSUB fib;
    SET res = fib_ret;
    
    SET fib_in = (- in 2);
    GOSUB fib;
    SET fib_ret = (+ res fib_ret);

BUS;

REM find 7th fib number;
SET fib_in = 7;
GOSUB fib;
PRINT fib_ret;
```

###Features

Here are some of the features **Pbasic** currently implements

 * Scoped labels and variables
 * Nested expressions (lisp style)
 * Integer lists
 * For, While, and If blocks
 * Input and Output with error checking
 * Subroutines with recursion
 * Inline assembly
 
### Running the Compiler

To build and run the **Pbasic** compiler from a POSIX shell, execute the commands:

```
$ make
$ CLASSPATH='src' java pbsc.PbscCompiler input.pbsc output.asm
```

To build and run the **pbasic-sos** simulator, execute the commands:

```
$ make
$ java sos.Sim file.asm
```

The **pbasic-sos** program also has several command line arguments. To see them:

```
java sos.Sim -h
```

To run the example programs, go to the `test` directory and run one of the shell scripts there.

### Language Documentation

####Overview
* A **Pbasic** program is comprised of several commands, separated by `;` characters.
* Newline characters are ignored and are treated as whitespace.
* Case is ignored. All characters are converted to lower case.
* Boolean values are represented by `INT`s. Negative numbers are false. Positive numbers (and 0) are true.

#### Comments
Comments are created by the `REM` commnd.
```
REM This is a comment
    Still a comment
    Continues until semicolon;
```

####Constants
Constants are created with the `DEFINE` command.
```
DEFINE #length = 10;
DEFINE #foo = 42;
DEFINE #bar = #foo;
```
Constants may be used in place of a literal integers anywhere in the program.

####Variables
There are two types of variables: Ints and Lists. They are created with the `INT` and `LIST` commands. The one exception is the `FOR` command which may implicitly create a variable.

Variables created with the `LIST` command must be given a length in `[<length>]`. The length must be a constant or literal.

Variables can only be accessed from the scope they were created in. Variables are scoped by `SUB`, `IF`, `WHILE`, and `FOR` blocks.

```
LIST bob[4];
IF 1;
	INT foobar;
FI;
INT foobar; REM different scope so this is a new var;
SET foobar = 0;
```

Lists are indexed by `[<int exp>]`

```
LIST foo[4];
PRINT foo[1];
```

Variables can be assigned to by useing the `SET` and `SETL` commands. Note the values to `SETL` must be constants or literals.

```
INT foo;
LIST bar[10];

SET foo = 4;
SET bar[foo] = 10;
SETL bar = 1 2 3 4 5 5 4 3 2 1;
REM You don't need to give a value to all positions;
SETL bar = 1 2 3;
```

####Expressions
Expressions in **Pbasic** are lisp-styled expressions and may be nested. They may be used anywhere where an integer would be appropriate (unless a constant or literal is required).

The possible opperators are:

 * Arithmetic - require two argurments: `+`, `-`, `*`, `/`, `MOD`
 * Comparison - require two arguments: `<`, `<=`, `>`, `>=`, `=`, `!=`
 * Boolean - require two argumenst (treated as booleans): `AND`, `OR`
 * Unary - require one argument (treated as boolean): `NOT`


```
INT foo;
LIST bar[10];

SET foo = (+ foo 1);
SET foo = (+ (- foo 1) bar[(+ 1 2)]);
```

####If Blocks
`IF` statements in **Pbasic** accept one expression which it treates as a boolean. It must have a corresponding `FI` statment to end the block and it may optionally have a corresponding `ELSE` statement.

```
IF 1;
	REM this always happens;
ELSE;
	REM this never happens;
FI;

IF (AND var1 (NOT var2));
	REM do stuff...;
FI;
```
####While Blocks
`WHILE` statements in **Pbasic** accept one expression which it treates as a boolean. It must have a corresponding `DONE` statement to end the block, and it may contain any number of `BREAK` statements.

```
WHILE (OR var1 (NOT var2));
	REM do stuff;
    BREAK;
DONE;
```

####For Blocks
`FOR` statements in **Pbasic** at minimum require a variable and a `TO` limit. They may optionally contain `FROM` and `STEP` arguments. They must be in the order `FROM`, `TO`, `STEP`. Note the `STEP` argument must be a constant or literal. If the variable doesn't exist, it will be created in the scope of the `FOR` block. They use `DONE` and `BREAK` statements like a `WHILE` loop.

```
FOR i FROM 10 TO 0 STEP -1;
	REM do stuff;
DONE

FOR i TO 0;
	REM do stuff;
DONE
```

####Labels
`LABEL` statements define a label which may be jumped to by either the `GOTO` command or some error handling routine of an I/O command. Labels are created in the global scope (even within `IF`, `FOR`, or `WHILE` blocks) unles they are created in a `SUB` block. This is to prevent jumping into a subroutine.

```
LABEL loop;
REM do stuff;
GOTO loop;
```

####Subroutines
`SUB` statements in **Pbasic** create a subroutine. They must have a name and a corresponding `BUS` command to end the subroutine. They may contain any number of `RETURN` statements which will prematurely end the subroutine. To execute a subroutine use the `GOSUB` command.

Each `SUB` block has its own scope for labels so you may jump out of them but not into them.

Note: you may not nest `SUB` blocks.

```
SUB foo;
	REM do stuff;
    RETURN;
    REM ...;
BUS;

SUB bar;
	GOSUB foo;
    REM do stuff;
BUS;

GOSUB bar;
```

When a subroutine calls another subroutine, all the local variables to the first subroutine get stored to the stack. When the second subroutine exits, they get popped from the stack. This allows recursion to take place.

There are no parameters or return values for subroutines. Global variables must be used for this, but if you are going to use recursion then the parameters must be saved to local variables.

####I/O
I/O can be acomplished through the commands.

 * `PRINT <int>` print an integer to console
 * `INPUT <var>` read an integer from keyboard
 * `READ <var> FROM <dev no>` read an integer from an open device
 * `WRITE <int> TO <dev no>` write an integer from an open device

Note the `PRINT` and `INPUT` commands automatically open and close the device. `READ` and `WRITE` require the device to already be open. To open or close a device use the `OPEN` and `CLOSE` commands.

All these commands can catch errors. Use the opional `ON <error> GOTO <label>` and `DEFAULT GOTO <label>` arguments to handle where to jump to on an error. `DEFAULT` is the default label to jump to if the error is not specifically handled by some `ON`. If there is no `DEFAULT` then the default is to ignore the error and continue on.

```
DEFINE #dev_no = 42;
DEFINE #already_open = 3;

OPEN #dev_no
ON #alredy_open GOTO ignore1
DEFAULT GOTO error;

WRITE 1 TO #dev_no DEFAULT GOTO error;
READ var FROM #dev_no DEFAULT GOTO error;

CLOSE #dev_no;

LABEL error;
PRINT -999;
```

####SOS commands
There are several SOS traps which may be executed from **Pbasic**.

 * `DUMP` Perform a core dump
 * `EXEC` Execute a process
 * `GETPID <var>` Put the pid of the current process into the variable
 * `YIELD` Perform a yeild so some other process may execute.
 
####Inline Pidgen
The `P` command creates an inline **Pidgen** instruction. Only one **Pidgen** instruction per `P` command.

Inside the `P` command, you may use `@var` to insert the address in memory of variable `var` into the command. You may also use `#const` to insert constants.

```
REM This is equivalent to SET foo=#bar;
P SET R0 @foo;
P SET R1 #bar;
P SAVE R1 R0;
```

### Implementation Documentation
Since different **SOS** implementations may set things up differently, the following must be true to use **Pbasics**.

 * R5 must be the Program Counter
 * R6 must be the Stack Pointer
 * The stack must start at the bottom and grow up
 * The instructions must be placed at the top of memory
 
The test implementation of **SOS** for Pbasic may be found at [pbasic-sos](https://github.com/sudobash1/pbasic-sos).
