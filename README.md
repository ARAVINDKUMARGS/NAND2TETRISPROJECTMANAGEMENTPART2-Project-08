# NAND2TETRISPROJECTMANAGEMENTPART2-Project-08

**PROJECT-08**

# NAND2TETRISPROJECTMANAGEMENTPART2-Project-8

## Project 8 – Jack Compiler Part II

**Project Code:** PROJECT08
**Course:** Build a Modern Computer from First Principles (Nand2Tetris Part II)
**Institution:** Hebrew University of Jerusalem

---

## Overview

Project 8 completes the **Jack Compiler** by implementing **full code generation**. Unlike Project 7, which produced parse trees, Project 8 **translates Jack programs into VM code**, which can then be executed by the **VM Emulator**.

This project connects **syntax analysis** from Project 7 with **executable code**, bridging the gap between high-level Jack programs and the Hack platform.

---

## Objectives

* Implement **symbol table management** for class-level, subroutine-level, and local variables.
* Generate **VM code** for all Jack statements and expressions.
* Support **control flow constructs** like `if`, `while`, and `return`.
* Support **subroutine calls**, including functions, methods, and constructors.
* Ensure compatibility with **OS routines** from Project 9.

---

## Folder Structure

```
Project8/
│── README.md
│── src/
│   └── JackCompiler.java
│── examples/
│   └── Main.jack
│── output/
│   └── Main.vm
│── docs/
│   └── CompilerII_Guide.pdf
```

---

## Getting Started

### Step 1: Compile the Compiler

```bash
cd Project8/src
javac JackCompiler.java
```

### Step 2: Run the Compiler on a Jack File

```bash
java JackCompiler ../examples/Main.jack
```

### Step 3: Check Output

* The compiler generates a `.vm` file in the `output/` folder.
* Load and run the `.vm` file in the **VM Emulator** to verify correct execution.

---

## Supported Features

### Statements

* `let` statements (variable assignment)
* `if` and `while` statements (control flow)
* `do` statements (subroutine calls)
* `return` statements

### Expressions

* Arithmetic: `+`, `-`, `*`, `/`
* Logical: `&`, `|`, `<`, `>`, `=`
* Unary operations: `-`, `~`

### Subroutines

* Functions, methods, and constructors
* Local and argument variable management
* Subroutine calls (`call`) with proper stack handling

---

## Example

**Input Jack code (`Main.jack`):**

```jack
class Main {
   function void main() {
      var int x;
      let x = 2 + 3;
      do Output.printInt(x);
      return;
   }
}
```

**Generated VM Code (`Main.vm`):**

```vm
function Main.main 1
push constant 2
push constant 3
add
pop local 0
call Output.printInt 1
return
```

---

## Notes

* The project produces VM code that **can be executed directly** in the VM Emulator.
* Fully integrates with OS routines from **Project 9**.
* Must be tested thoroughly using provided examples for correctness.
* Ensure all subroutine calls, control flow statements, and expressions generate proper VM code.

---

## Author

**Aravind Kumar GS**
Email: `aravindkumar06062006@gmail.com`

---

## License

Educational purposes only. Do **not distribute** or claim as your own work.
