# Learn x86-64 assembly by writing a GUI from scratch

Most people think assembly is only to be used to write toy programs for learning purposes, or to write a highly optimized version of a specific function inside a codebase written in a high-level language.

Well, what if we wrote a whole program in assembly that opens a GUI window? It will be the hello world of the GUI world, but that still counts. Here is what we are working towards:

![Result](https://gaultier.github.io/blog/x11_x64_final.png)

I wanted to expand my knowledge of assembly and by doing something fun and motivating. It all originated from the observation that so many program binaries today are very big, often over 30 Mib (!), and I asked myself: How small a binary can be for a (very simplistic) GUI? Well, it turns out, very little. Spoiler alert: around 1 KiB!

> I am by no means an expert in assembly or in X11. I just hope to provide an entertaining, approachable article, something a beginner can understand. Something I wished I had found when I was learning those topics. If you spot an error, please open a [Github issue](https://github.com/gaultier/blog)!

**Table of Contents**

- [What do we need?](https://gaultier.github.io/blog/x11_x64.html#what-do-we-need)
- [X11 basics](https://gaultier.github.io/blog/x11_x64.html#x11-basics)
- [Main in x64 assembly](https://gaultier.github.io/blog/x11_x64.html#main-in-x64-assembly)
- A stack primer
  - [A small stack example](https://gaultier.github.io/blog/x11_x64.html#a-small-stack-example)
- [Opening a socket](https://gaultier.github.io/blog/x11_x64.html#opening-a-socket)
- [Connecting to the server](https://gaultier.github.io/blog/x11_x64.html#connecting-to-the-server)
- [Sending data over the socket](https://gaultier.github.io/blog/x11_x64.html#sending-data-over-the-socket)
- [Generating ids](https://gaultier.github.io/blog/x11_x64.html#generating-ids)
- [Opening a font](https://gaultier.github.io/blog/x11_x64.html#opening-a-font)
- [Creating a graphical context](https://gaultier.github.io/blog/x11_x64.html#creating-a-graphical-context)
- [Creating the window](https://gaultier.github.io/blog/x11_x64.html#creating-the-window)
- [Mapping the window](https://gaultier.github.io/blog/x11_x64.html#mapping-the-window)
- [Polling for server messages](https://gaultier.github.io/blog/x11_x64.html#polling-for-server-messages)
- [Drawing text](https://gaultier.github.io/blog/x11_x64.html#drawing-text)
- [The end](https://gaultier.github.io/blog/x11_x64.html#the-end)
- [Addendum: the full code](https://gaultier.github.io/blog/x11_x64.html#addendum-the-full-code)

## What do we need?

I will be using the `nasm` assembler which is simple, cross-platform, fast, and has quite a readable syntax.

For the GUI, I will be using X11 since I am based on Linux and it has some interesting properties that make it easy to do without external libraries. If you are running Wayland, it should work with XWayland out of the box, and perhaps also on macOS with XQuartz, but I have not tested those.

Note that the only difference between \*nix operating systems in the context of this program is the system call values. Since I am based on Linux I will be using the Linux system call values, but ‘porting’ this program to, say, FreeBSD, would only require to change those values, possibly using the nasm macros:

```x86asm
%ifdef linux
  %define SYSCALL_EXIT 1
%elifdef freebsd
  %define SYSCALL_EXIT 60
%endif
```

> `%define` and its variants are part of the macro system in `nasm`, which is powerful but we will only use it here to define constants, just like in C: `#define FOO 3`.

No need for additional tooling to cross-compile, issues with dynamic libraries, libc differences, etc. Just compile on Linux by defining the right variable on the command line, send the binary to your friend on FreeBSD, and it just works(tm). That’s refreshing.

So let’s dive in!

## X11 basics

X11 is a server accessible over the network that handles windowing and rendering inside those windows. A client opens a socket, connects to the server, and sends commands in a specific format to open a window, draw shapes, text, etc. The server sends message about errors or events to the client.

Most applications will want to use `libX11` or `libxcb` which offer a C API, but we want to do that ourselves.

Where the server lives is actually not relevant for a client, it might run on the same machine or in a datacenter far far away. Of course, in the context of a desktop computer in 2023, it will be running on the same machine, but that’s a detail.

The [official documentation](https://www.x.org/releases/X11R7.7/doc/xproto/x11protocol.html) is pretty good, so in doubt we can refer to it.

## Main in x64 assembly

Let’s start slow with minimal program that simply exits with 0, and build from there.

First, we tell nasm we are writing a 64 bit program and that we target x86_64. Then, we need a main function, which we call `_start` and needs to be visible since this is the entry point of our program (hence the `global` keyword):

```x86asm
; Comments start with a semicolon!
BITS 64 ; 64 bits.
CPU X64 ; Target the x86_64 family of CPUs.

section .text
global _start
_start:
  xor rax, rax ; Set rax to 0. Not actually needed, it's just to avoid having an empty body.
```

`section .text` is telling `nasm` and the linker, that what follows is code that should be placed in the text section of the executable.

We will soon have a `section .data` for our global variables.

Note that those section usually get mapped by the OS to different pages in memory with different permissions (visible with `readelf -l`) so that the text section is not writable and the data section is not executable, but that varies from OS to OS.

The `_start` function has a body that does nothing for now, but not for long. The actual name of the main function is actually up to us, it’s just that `start` or `_start` is usual.

We build and run our little program like this:

```cpp
$ nasm -f elf64 -g main.nasm && ld main.o -static -o main
```

`nasm` actually only produces an object file, so to get an executable out of it, we need to invoke the linker `ld`. The flag `-g` is telling `nasm` to produce debugging information which is immensely useful when writing raw assembly, since firing the debugger is often our only recourse in face of a bug.

_To remove the debugging information, we can pass `-s` to the linker, for example when we are about to ship our program and want to save a few KiB._

We finally have an executable:

```shell
$ file ./main
main: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), statically linked, with debug_info, not stripped
```

We can see the different sections with `readelf -a ./main`, and it tells us that the `.text` section, which contains our code, is only 3 bytes long.

Now, if we try to run our program, it will segfault. That’s because we are expected by the operating system to exit (using the exit system call) ourselves. That’s what libc does for us in C programs, so let’s handle that:

```x86asm
%define SYSCALL_EXIT 60

global _start:
_start:
  mov rax, SYSCALL_EXIT
  mov rdi, 0
  syscall
```

> `nasm` uses the intel syntax: `<instruction> <destination>, <source>`, so `mov rdi, 0` puts 0 into the register `rdi`. Other assemblers use the AT&T syntax which swaps the source and destination. My advice: pick one syntax and one assembler and stick to it, both syntaxes are fine and most tools have some support for both.

Following the System V ABI, which is required on Linux and other Unices for system calls, invoking a system call requires us to put the system call code in the register `rax`, the parameters to the syscall (up to 6) in the registers `rdi`, `rsi`, `rdx`, `rcx`, `r8`, `r9`, and additional parameters, if any, on the stack (which will not happen in this program so we can forget about it). We then use the instruction `syscall` and check `rax` for the return value, `0` usually meaning: no error.

_Note that Linux has a ‘fun’ difference, which is that the fourth parameter of a system call is actually passed using the register `r10`._

> Note that the System V ABI is required when making system calls and when interfacing with C but we are free to use whatever conventions we want in our own assembly code. For a long time, Go was using a different calling convention than the System V ABI, for example, when calling functions (passing arguments on the stack). Most tools (debuggers, profilers) expect the System V ABI though, so I recommend sticking to it.

Back to our program: when we run it, we see…nothing. That’s because everything went well, true to the UNIX philosophy!

We can check the exit code:

```shell
$ ./main; echo $?
0
```

Changing `mov rdi, 0` to `mov rdi, 8` will now result in:

```shell
$ ./main; echo $?
8
```

Another way to observe system calls made by a program is with `strace`, which will also prove very useful when troubleshooting. On some BSD, its equivalent is `truss` or `dtruss`.

```cpp
$ strace ./main
execve("./main", ["./main"], 0x7ffc60e6bf10 /* 60 vars */) = 0
exit(8)                                 = ?
+++ exited with 8 +++
```

Let’s change it back to 0 and continue.

## A stack primer

Before we can continue, we need to know the basics of how the stack works in assembly since we have no friendly compiler to do that for us.

**The three most important things about the stack are:**

- It grows downwards: to reserve more space on the stack, we decrease the value of `rsp`
- A function must restore the stack pointer to its original value before the function returns, meaning, either remember the original value and set `rsp` to this, or, match every decrement by an increment of the same value.
- Before a function call, the stack pointer needs to be 16 bytes aligned, according to the System V ABI. Also, at the very beginning of a function, the stack pointer value is: `16*N + 8`. That’s because before the function call, its value was 16 byte aligned, i.e. `16*N`, and the `call` instruction pushes on the stack the current location (the register `rip`, which is 8 bytes long), to know where to jump when the called function returns.

Not abiding by those rules will result in nasty crashes, so be warned. That’s because the location of where to jump when the function returns will be likely overwritten and the program will jump to the wrong location. That, or the stack content will be overwritten and the program will operate on wrong values. Bad either way.

### A small stack example

Let’s write a function that prints `hello` to the standard out, using the stack, to learn the ropes.

We need to reserve (at least) 5 bytes on the stack, since that’s the length in bytes of `hello`.

The stack looks like this:

| ... |
| --- |
| rbp |
| o   |
| l   |
| l   |
| e   |
| h   |

And `rsp` points to the bottom of it.

Here’s how we access each element:

| Memory location (example) | Assembly code | Stack element |
| ------------------------- | ------------- | ------------- |
| 0x1016                    |               | ...           |
| 0x1015                    | rsp + 5       | rbp           |
| 0x1014                    | rsp + 4       | o             |
| 0x1013                    | rsp + 3       | l             |
| 0x1012                    | rsp + 2       | l             |
| 0x1011                    | rsp + 1       | e             |
| 0x1010                    | rsp + 0       | h             |

We then pass the address on the stack of the beginning of the string to the `write` syscall, as well as its length:

```x86asm
%define SYSCALL_WRITE 1
%define STDOUT 1

print_hello:
  push rbp ; Save rbp on the stack to be able to restore it at the end of the function.
  mov rbp, rsp ; Set rbp to rsp

  sub rsp, 5 ; Reserve 5 bytes of space on the stack.
  mov BYTE [rsp + 0], 'h' ; Set each byte on the stack to a string character.
  mov BYTE [rsp + 1], 'e'
  mov BYTE [rsp + 2], 'l'
  mov BYTE [rsp + 3], 'l'
  mov BYTE [rsp + 4], 'o'

  ; Make the write syscall
  mov rax, SYSCALL_WRITE
  mov rdi, STDOUT ; Write to stdout.
  lea rsi, [rsp] ; Address on the stack of the string.
  mov rdx, 5 ; Pass the length of the string which is 5.
  syscall

  add rsp, 5 ; Restore the stack to its original value.

  pop rbp ; Restore rbp
  ret
```

> `lea destination, source` loads the effective address of the source into the destination, which is how C pointers are implemented. To dereference a mememory location we use square brackets. So, assuming we just have loaded an address into `rdi` with `lea`, e.g. `lea rdi, [hello_world]`, and we want to store the value at the address into `rax`, we do: `mov rax, [rdi]`. We usually have to tell `nasm` how many bytes to dereference with `BYTE`, `WORD`, `DWORD`, `QWORD` so: `mov rax, DWORD [rdi]`, because `nasm` does not keep track of the sizes of each variable. That’s also what the C compiler does when we dereference a `int8_t`, `int16_t`, `int32_t`, and `int64_t` pointer, respectively.

There is a lot to unpack here.

First, what is `rbp`? That’s a register like any other. But, you can choose to follow the convention of not using this register like the other registers, to store arbitrary values, and instead, use it to store a linked list of call frames. That’s a lot of words.

Basically, at the very beginning of a function, the value of `rbp` is stored on the stack (that’s `push rbp`). Since `rbp` stores an address (the address of the frame that’s called us), we are storing on the stack the address of the caller in a known location.

Immediately after that, we set `rbp` to `rsp`, that is, to the stack pointer at the beginning of the function. `push rbp` and `mov rbp, rsp` are thus usually referred to as the function prolog.

For the rest of the function body, we treat `rbp` as a constant and only decrease `rsp` if we need to reserve space on the stack.

So if function A calls function B which in turn calls function C, and each function stores on the stack the address of the caller frame, we know where to find on the stack the address of each. Thus, we can print a stack trace in any location of our program simply by inspecting the stack. Pretty nifty. That’s already very useful to profilers and other similar tools.

We must not forget of course, just before we exit the function, to restore `rbp` to its original value (which is still on the stack at that point): that’s `pop rbp`. This is also known as the function epilog. Another way to look at it is that we remove the last element of the linked list of call frames, since we are exiting the leaf function.

Don’t worry if you have not fully understood everything, just remember to always have the function epilogs and prologs and you’ll be fine:

```x86asm
my_function:
  push rbp
  mov rbp, rsp

  sub rsp, N

  [...]


  add rsp, N
  pop rbp
  ret
```

**Note**: There is an optimization method that uses `rbp` as a standard register (with a C compiler, that’s the flag `-fomit-frame-pointer`), which means we lose the information about the call stack. My advice is: never do this, it is no worth it.

> Wait, but didn’t you say the stack needs to be 16 byte aligned (that is, a multiple of 16)? Last time I checked, 5 is not really a multiple of 16!

Good catch! The only reason why this program works, is that `print_hello` is a leaf function, meaning it does not call another function. Remember, the stack needs to be 16 bytes aligned when we do a `call`!

So the correct way would be:

```x86asm
print_hello:
  push rbp
  mov rbp, rsp

  sub rsp, 16
  mov BYTE [rsp + 0], 'h'
  mov BYTE [rsp + 1], 'e'
  mov BYTE [rsp + 2], 'l'
  mov BYTE [rsp + 3], 'l'
  mov BYTE [rsp + 4], 'o'

  mov rax, SYSCALL_WRITE
  mov rdi, STDOUT
  lea rsi, [rsp]
  mov rdx, 5
  syscall

  call print_world

  add rsp, 16

  pop rbp
  ret
```

Since when we enter the function, the value of `rsp` is `16*N+8`, and pushing `rbp` increases it by 8, the stack pointer is 16 bytes aligned at the point of `sub rsp, 16`. Decrementing it by 16 (or a multiple of 16) keeps it 16 bytes aligned.

We know can safely call another function from within `print_hello`:

```x86asm
print_world:
  push rbp
  mov rbp, rsp

  sub rsp, 16
  mov BYTE [rsp + 0], ' '
  mov BYTE [rsp + 1], 'w'
  mov BYTE [rsp + 2], 'o'
  mov BYTE [rsp + 3], 'r'
  mov BYTE [rsp + 4], 'l'
  mov BYTE [rsp + 5], 'd'

  mov rax, SYSCALL_WRITE
  mov rdi, STDOUT
  lea rsi, [rsp]
  mov rdx, 6
  syscall

  add rsp, 16

  pop rbp
  ret

print_hello:
  push rbp
  mov rbp, rsp

  sub rsp, 16
  mov BYTE [rsp + 0], 'h'
  mov BYTE [rsp + 1], 'e'
  mov BYTE [rsp + 2], 'l'
  mov BYTE [rsp + 3], 'l'
  mov BYTE [rsp + 4], 'o'

  mov rax, SYSCALL_WRITE
  mov rdi, STDOUT
  lea rsi, [rsp]
  mov rdx, 5
  syscall

  call print_world

  add rsp, 16

  pop rbp
  ret
```

And we get `hello world` as an output.

Now, try to do `sub rsp, 5` in `print_hello`, and your program _may_ crash. There is no guarantee, that’s what makes it hard to track down.

My advice is:

- Always use the standard function prologs and epilogs
- Always increment/decrement `rsp` by (a multiple of) 16
- If you have to decrement `rsp` by a value that’s unknown at compile time (similar to how `alloca()` works in C), you can `and rsp, -16` to 16 bytes align it.
- Address items on the stack relative to `rsp`, i.e. `mov BYTE [rsp + 4], 'o'`

And you’ll be safe.

The last point is interesting, see for yourself:

```bash
(gdb) p -100 & -16
$1 = -112
(gdb) p -112 & -16
$2 = -112
```

Which translates in assembly to:

```x86asm
sub rsp, 100
and rsp, -16
```

Finally, following those conventions means that our assembly functions can be safely called from C or other languages following the [System V ABI](https://wiki.osdev.org/System_V_ABI), without any modification, which is great.

_I have not talked about the red zone which is a 128 byte region at the bottom of the stack which our program is free to use as it pleases without having to change the stack pointer. In my opinion, it is not helpful and creates hard to track bugs, so I do not recommend to use it. To disable it entirely, run: `nasm -f elf64 -g main.nasm && cc main.o -static -o main -mno-red-zone -nostdlib`_.

## Opening a socket

We now are ready to open a socket with the `socket(2)` syscall, so we add a few constants, taken from the libc headers (_note that those values might actually be different on a different Unix, I have not checked. Again, a few `%ifdef` can easily remedy this discrepancy_):

```x86asm
%define AF_UNIX 1
%define SOCK_STREAM 1

%define SYSCALL_SOCKET 41
```

The `AF_UNIX` constant means we want a Unix domain socket, and `SOCK_STREAM` means TCP. We use a domain socket since we now that our server is running on the same machine and it should be faster, but we could change it to `AF_INET` to connect to a remote IPv4 address for example.

We then fill the relevant registers with those values and invoke the system call:

```x86asm
  mov rax, SYSCALL_SOCKET
  mov rdi, AF_UNIX ; Unix socket.
  mov rsi, SOCK_STREAM ; Tcp-like.
  mov rdx, 0 ; Automatic protocol.
  syscall
```

The C equivalent would be: `socket(AF_UNIX, SOCK_STREAM, 0);`. So you see that if we fill the registers in the same order as the C function parameters, we stay close to what C code would do.

The whole program now looks like this:

```x86asm
BITS 64 ; 64 bits.
CPU X64 ; Target the x86_64 family of CPUs.

section .text

%define AF_UNIX 1
%define SOCK_STREAM 1

%define SYSCALL_SOCKET 41
%define SYSCALL_EXIT 60

global _start:
_start:
  ; open a unix socket.
  mov rax, SYSCALL_SOCKET
  mov rdi, AF_UNIX ; Unix socket.
  mov rsi, SOCK_STREAM ; Tcp-like.
  mov rdx, 0 ; automatic protocol.
  syscall


  ; The end.
  mov rax, SYSCALL_EXIT
  mov rdi, 0
  syscall
```

Building and running it under `strace` shows that it works and we get a socket with the file descriptor `3` (in this case, it might be different for you if you are following at home):

```cpp
$ nasm -f elf64 -g main.nasm && ld main.o -static -o main
$ strace ./main
execve("./main", ["./main"], 0x7ffe54dfe550 /* 60 vars */) = 0
socket(AF_UNIX, SOCK_STREAM, 0)         = 3
exit(0)                                 = ?
+++ exited with 0 +++
```

## Connecting to the server

Now that we have created a socket, we can connect to the server with the `connect(2)` system call.

It’s a good time to extract that logic in its own little function, just like in any other high-level language.

```x86asm
x11_connect_to_server:
  ; TODO
```

In assembly, a function is simply a label we can jump to. But for clarity, both for readers of the code and tools, we can add a hint that this is a real function we can call, like this: `call x11_connect_to_server`. This will improve the call stack for example when using `strace -k`. This hint has the form (in `nasm`): `static <name of the function>:function`.

Of course, we also need to add our standard function prolog and epilog:

```x86asm
x11_connect_to_server:
static x11_connect_to_server:function
  push rbp
  mov rbp, rsp

  pop rbp
  ret
```

An additional help when reading functions in assembly code is adding comments describing what parameters they accept and what is the return value, if any. Since there is no language level feature for this, we resort to comments:

```x86asm
; Create a UNIX domain socket and connect to the X11 server.
; @returns The socket file descriptor.
x11_connect_to_server:
static x11_connect_to_server:function
  push rbp
  mov rbp, rsp

  pop rbp
  ret
```

First, let’s move the socket creation logic to our function and call it in the program:

```x86asm
; Create a UNIX domain socket and connect to the X11 server.
; @returns The socket file descriptor.
x11_connect_to_server:
static x11_connect_to_server:function
  push rbp
  mov rbp, rsp

  ; Open a Unix socket: socket(2).
  mov rax, SYSCALL_SOCKET
  mov rdi, AF_UNIX ; Unix socket.
  mov rsi, SOCK_STREAM ; Tcp-like.
  mov rdx, 0 ; Automatic protocol.
  syscall

  cmp rax, 0
  jle die

  mov rdi, rax ; Store socket fd in `rdi` for the remainder of the function.

  pop rbp
  ret

die:
  mov rax, SYSCALL_EXIT
  mov rdi, 1
  syscall

_start:
global _start:function
  call x11_connect_to_server

  ; The end.
  mov rax, SYSCALL_EXIT
  mov rdi, 0
  syscall
```

The error checking is very simplistic: we only check that the return value of the system call (in `rax`) is what we expect, otherwise we exit the program with a non-zero code by jumping to the `die` section.

> `jle` is a conditional jump, which inspects global flags, hopefully set just before with `cmp` or `test`, and jumps to a label if the condition is true. Here, we compare the returned value with 0, and if it is lower or equal to 0, we jump to the error label. That’s how we implement conditionals and loops.

---

Ok, we can finally connect to the server now. The `connect(2)` system call takes the address of a `sockaddr_un` structure as the second argument. This structure is too big to fit in a register.

This is the first syscall we encounter that needs to be passed a pointer, in other words, the address of a region in memory. That region can be on the stack or on the heap, or even be our own executable mapped in memory. That’s assembly, we get to do whatever we want.

Since we want to keep things simple and fast, we will store everything in this program on the stack. And since we have 8 MiB of it (according to `limit`, on my machine, that is), it’ll be plenty enough. Actually, the most space we will need on the stack in this program will be 32 KiB.

The size of the `sockaddr_un` structure is 110 bytes, so we reserve 112 to align `rsp` to 16 bytes.

> Nasm does have structs, but they are rather a way to define offsets with a name, than structures like in C with a specific syntax to address a specific field. For the sake of simplicity, I’ll use the manual way, without `nasm` structs.

We set the first 2 bytes of this structure to `AF_UNIX` since this is a domain socket. Then comes the path of the Unix domain socket which X11 expects to be in a certain format. We want to display our window on the first monitor starting at 0, so the string is: `/tmp/.X11-unix/X0`.

In C, we would do:

```csharp
  const sockaddr_un addr = {.sun_family = AF_UNIX,
                            .sun_path = "/tmp/.X11-unix/X0"};
  const int res =
      connect(x11_socket_fd, (const struct sockaddr *)&addr, sizeof(addr));
```

How do we translate that to assembly, especially the string part?

We could set each byte to each character of the string in the structure, on the stack, manually, one by one. Another [way](https://en.wikibooks.org/wiki/X86_Assembly/Data_Transfer#Move_String) to do it is to use the `rep movsb` idiom, which instructs the CPU to copy a character from a string A to another string B, N times. This is exactly what we need!

The way it works is:

- We put the string in the `.rodata` section (same as the data section but read-only)
- We load its address in `rsi` (it’s the source)
- We load the address of the string in the structure on the stack in `rdi` (it’s the destination)
- We set `rcx` to the number of bytes to be copied
- We use `cld` to clear the `DF` flag to ensure the copy is done forwards (since it can also be done backwards)
- We call `rep movsb` and voila

It’s basically `memcpy` from C.

> This is a interesting case: we can see that some instructions expect some of their operands to be in certain registers and there is no way around it. So, we have to plan ahead and expect those registers to be overwritten. If we need to keep their original values around, we have to store those values elsewhere, for example on the stack (that’s called spilling) or in other registers. This is a broader topic of register allocation which is NP-hard! In small functions, it’s manageable though.

First, the `.rodata` section:

```x86asm
section .rodata

sun_path: db "/tmp/.X11-unix/X0", 0
static sun_path:data
```

Then we copy the string:

```x86asm
  mov WORD [rsp], AF_UNIX ; Set sockaddr_un.sun_family to AF_UNIX
  ; Fill sockaddr_un.sun_path with: "/tmp/.X11-unix/X0".
  lea rsi, sun_path
  mov r12, rdi ; Save the socket file descriptor in `rdi` in `r12`.
  lea rdi, [rsp + 2]
  cld ; Move forward
  mov ecx, 19 ; Length is 19 with the null terminator.
  rep movsb ; Copy.
```

> `ecx` is the 32 bit form of the register `rcx`, meaning we only set here the lower 32 bits of the 64 bit register. [This handy table](https://wiki.osdev.org/CPU_Registers_x86-64) lists all of the forms for all of the registers. But be cautious of the pitfall case of only setting a value in part of a register, and then using the whole register later. The rest of the bits that have not been set will contain some past value, which is hard to troubleshoot. The solution is to use `movzx` to zero extend, meaning setting the rest of the bits to 0. A good way to visualize this is to use `info registers` within gdb, and that will display for each register the value for each of its forms, e.g. for `rcx`, it will display the value for `rcx`, `ecx`, `cx`, `ch`, `cl`.

Then, we do the syscall, check the returned value, exit the program if the value is not 0, and finally return the socket file descriptor, which will be used every time in the rest of the program when talking to the X11 server.

Everything together, it looks like:

```x86asm
; Create a UNIX domain socket and connect to the X11 server.
; @returns The socket file descriptor.
x11_connect_to_server:
static x11_connect_to_server:function
  push rbp
  mov rbp, rsp

  ; Open a Unix socket: socket(2).
  mov rax, SYSCALL_SOCKET
  mov rdi, AF_UNIX ; Unix socket.
  mov rsi, SOCK_STREAM ; Tcp-like.
  mov rdx, 0 ; Automatic protocol.
  syscall

  cmp rax, 0
  jle die

  mov rdi, rax ; Store socket fd in `rdi` for the remainder of the function.

  sub rsp, 112 ; Store struct sockaddr_un on the stack.

  mov WORD [rsp], AF_UNIX ; Set sockaddr_un.sun_family to AF_UNIX
  ; Fill sockaddr_un.sun_path with: "/tmp/.X11-unix/X0".
  lea rsi, sun_path
  mov r12, rdi ; Save the socket file descriptor in `rdi` in `r12`.
  lea rdi, [rsp + 2]
  cld ; Move forward
  mov ecx, 19 ; Length is 19 with the null terminator.
  rep movsb ; Copy.

  ; Connect to the server: connect(2).
  mov rax, SYSCALL_CONNECT
  mov rdi, r12
  lea rsi, [rsp]
  %define SIZEOF_SOCKADDR_UN 2+108
  mov rdx, SIZEOF_SOCKADDR_UN
  syscall

  cmp rax, 0
  jne die

  mov rax, rdi ; Return the socket fd.

  add rsp, 112
  pop rbp
  ret
```

We are ready to talk to the X11 server!

## Sending data over the socket

There is the `send(2)` syscall to do this, but we can keep it simple and use the generic `write(2)` syscall instead. Either way works.

```x86asm
%define SYSCALL_WRITE 1
```

The C structure for the handshake looks like this:

```rust
typedef struct {
  u8 order;
  u8 pad1;
  u16 major, minor;
  u16 auth_proto, auth_data;
  u16 pad2;
} x11_connection_req_t;
```

`pad*` fields can be ignored since they are padding and their value is not read by the server.

For our handshake, we need to set the `order` to be `l`, that is, little-endian, since X11 can be told to interpret message as big or little endian. Since x64 is little-endian, we do not want to have a endianness translation layer and so we stick to little-endian.

We also need to set the `major` field, which is the version, to `11`. I’ll leave it to the reader to guess why.

In C, we would do:

```cpp
  x11_connection_req_t req = {.order = 'l', .major = 11};
```

This structure is only 12 bytes long, but since we will have to read the response from the server which is quite big (around 14 KiB during my testing), we will right away reserve a lot of space on the stack, 32 KiB, to be safe:

```x86asm
  sub rsp, 1<<15
  mov BYTE [rsp + 0], 'l' ; Set order to 'l'.
  mov WORD [rsp + 2], 11 ; Set major version to 11.
```

Then we send it to the server:

```x86asm
  ; Send the handshake to the server: write(2).
  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 12*8
  syscall

  cmp rax, 12*8 ; Check that all bytes were written.
  jnz die
```

After that, we read the server response, which should be at first 8 bytes:

```x86asm
  ; Read the server response: read(2).
  ; Use the stack for the read buffer.
  ; The X11 server first replies with 8 bytes. Once these are read, it replies with a much bigger message.
  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 8
  syscall

  cmp rax, 8 ; Check that the server replied with 8 bytes.
  jnz die

  cmp BYTE [rsp], 1 ; Check that the server sent 'success' (first byte is 1).
  jnz die
```

The first byte in the server response is `0` for failure and `1` for success (and `2` for authentication but we will not need it here).

The server send sends a big message with a lot of general information, which we will need for later, so we store certain fields in global variables located in the data section.

First we add those variables, each 4 bytes big:

```x86asm
section .data

id: dd 0
static id:data

id_base: dd 0
static id_base:data

id_mask: dd 0
static id_mask:data

root_visual_id: dd 0
static root_visual_id:data
```

Then we read the server response, and skip over the parts we are not interested in. This boils down to incrementing a pointer by a dynamic value, a few times. Note that since we do not do any checks here, that would be a great attack vector to trigger a stack overflow or such in our program.

```x86asm
  ; Read the rest of the server response: read(2).
  ; Use the stack for the read buffer.
  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 1<<15
  syscall

  cmp rax, 0 ; Check that the server replied with something.
  jle die

  ; Set id_base globally.
  mov edx, DWORD [rsp + 4]
  mov DWORD [id_base], edx

  ; Set id_mask globally.
  mov edx, DWORD [rsp + 8]
  mov DWORD [id_mask], edx

  ; Read the information we need, skip over the rest.
  lea rdi, [rsp] ; Pointer that will skip over some data.

  mov cx, WORD [rsp + 16] ; Vendor length (v).
  movzx rcx, cx

  mov al, BYTE [rsp + 21]; Number of formats (n).
  movzx rax, al ; Fill the rest of the register with zeroes to avoid garbage values.
  imul rax, 8 ; sizeof(format) == 8

  add rdi, 32 ; Skip the connection setup
  add rdi, rcx ; Skip over the vendor information (v).
  add rdi, rax ; Skip over the format information (n*8).

  mov eax, DWORD [rdi] ; Store (and return) the window root id.

  ; Set the root_visual_id globally.
  mov edx, DWORD [rdi + 32]
  mov DWORD [root_visual_id], edx
```

All together:

```x86asm
; Send the handshake to the X11 server and read the returned system information.
; @param rdi The socket file descriptor
; @returns The window root id (uint32_t) in rax.
x11_send_handshake:
static x11_send_handshake:function
  push rbp
  mov rbp, rsp

  sub rsp, 1<<15
  mov BYTE [rsp + 0], 'l' ; Set order to 'l'.
  mov WORD [rsp + 2], 11 ; Set major version to 11.

  ; Send the handshake to the server: write(2).
  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 12*8
  syscall

  cmp rax, 12*8 ; Check that all bytes were written.
  jnz die

  ; Read the server response: read(2).
  ; Use the stack for the read buffer.
  ; The X11 server first replies with 8 bytes. Once these are read, it replies with a much bigger message.
  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 8
  syscall

  cmp rax, 8 ; Check that the server replied with 8 bytes.
  jnz die

  cmp BYTE [rsp], 1 ; Check that the server sent 'success' (first byte is 1).
  jnz die

  ; Read the rest of the server response: read(2).
  ; Use the stack for the read buffer.
  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 1<<15
  syscall

  cmp rax, 0 ; Check that the server replied with something.
  jle die

  ; Set id_base globally.
  mov edx, DWORD [rsp + 4]
  mov DWORD [id_base], edx

  ; Set id_mask globally.
  mov edx, DWORD [rsp + 8]
  mov DWORD [id_mask], edx

  ; Read the information we need, skip over the rest.
  lea rdi, [rsp] ; Pointer that will skip over some data.

  mov cx, WORD [rsp + 16] ; Vendor length (v).
  movzx rcx, cx

  mov al, BYTE [rsp + 21]; Number of formats (n).
  movzx rax, al ; Fill the rest of the register with zeroes to avoid garbage values.
  imul rax, 8 ; sizeof(format) == 8

  add rdi, 32 ; Skip the connection setup
  add rdi, rcx ; Skip over the vendor information (v).
  add rdi, rax ; Skip over the format information (n*8).

  mov eax, DWORD [rdi] ; Store (and return) the window root id.

  ; Set the root_visual_id globally.
  mov edx, DWORD [rdi + 32]
  mov DWORD [root_visual_id], edx

  add rsp, 1<<15
  pop rbp
  ret
```

> From this point on, I will assume you are familiar with the basics of assembly and X11 and will not go as much into details.

## Generating ids

When creating resources on the server-side, we usually first generate an id on the client side, and send that id to the server when creating the resource.

We store the current id in a global variable and increment it each time a new id is generated.

This is how we do it:

```x86asm
; Increment the global id.
; @return The new id.
x11_next_id:
static x11_next_id:function
  push rbp
  mov rbp, rsp

  mov eax, DWORD [id] ; Load global id.

  mov edi, DWORD [id_base] ; Load global id_base.
  mov edx, DWORD [id_mask] ; Load global id_mask.

  ; Return: id_mask & (id) | id_base
  and eax, edx
  or eax, edi

  add DWORD [id], 1 ; Increment id.

  pop rbp
  ret
```

## Opening a font

To open a font, which is a prerequisite to draw text, we send a message to the server specifying (part of) the name of the font we want, and the server will select a matching font.

To play with another font, you can use `xfontsel` which displays all the font names that the X11 server knows about.

First, we generate an id for the font locally, and then we send it alongside the font name.

```x86asm
; Open the font on the server side.
; @param rdi The socket file descriptor.
; @param esi The font id.
x11_open_font:
static x11_open_font:function
  push rbp
  mov rbp, rsp

  %define OPEN_FONT_NAME_BYTE_COUNT 5
  %define OPEN_FONT_PADDING ((4 - (OPEN_FONT_NAME_BYTE_COUNT % 4)) % 4)
  %define OPEN_FONT_PACKET_U32_COUNT (3 + (OPEN_FONT_NAME_BYTE_COUNT + OPEN_FONT_PADDING) / 4)
  %define X11_OP_REQ_OPEN_FONT 0x2d

  sub rsp, 6*8
  mov DWORD [rsp + 0*4], X11_OP_REQ_OPEN_FONT | (OPEN_FONT_NAME_BYTE_COUNT << 16)
  mov DWORD [rsp + 1*4], esi
  mov DWORD [rsp + 2*4], OPEN_FONT_NAME_BYTE_COUNT
  mov BYTE [rsp + 3*4 + 0], 'f'
  mov BYTE [rsp + 3*4 + 1], 'i'
  mov BYTE [rsp + 3*4 + 2], 'x'
  mov BYTE [rsp + 3*4 + 3], 'e'
  mov BYTE [rsp + 3*4 + 4], 'd'


  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, OPEN_FONT_PACKET_U32_COUNT*4
  syscall

  cmp rax, OPEN_FONT_PACKET_U32_COUNT*4
  jnz die

  add rsp, 6*8

  pop rbp
  ret
```

## Creating a graphical context

Since an application in X11 can have multiple windows, we first need to create a graphical context containing the general information. When we create a window, we refer to this graphical context by id.

Again, we need to generate an id for the graphical context to be.

X11 stores a hierarchy of windows, so when creating the graphical context, we also need to give it the root window id (i.e. the parent id).

```x86asm
; Create a X11 graphical context.
; @param rdi The socket file descriptor.
; @param esi The graphical context id.
; @param edx The window root id.
; @param ecx The font id.
x11_create_gc:
static x11_create_gc:function
  push rbp
  mov rbp, rsp

  sub rsp, 8*8

%define X11_OP_REQ_CREATE_GC 0x37
%define X11_FLAG_GC_BG 0x00000004
%define X11_FLAG_GC_FG 0x00000008
%define X11_FLAG_GC_FONT 0x00004000
%define X11_FLAG_GC_EXPOSE 0x00010000

%define CREATE_GC_FLAGS X11_FLAG_GC_BG | X11_FLAG_GC_FG | X11_FLAG_GC_FONT
%define CREATE_GC_PACKET_FLAG_COUNT 3
%define CREATE_GC_PACKET_U32_COUNT (4 + CREATE_GC_PACKET_FLAG_COUNT)
%define MY_COLOR_RGB 0x0000ffff

  mov DWORD [rsp + 0*4], X11_OP_REQ_CREATE_GC | (CREATE_GC_PACKET_U32_COUNT<<16)
  mov DWORD [rsp + 1*4], esi
  mov DWORD [rsp + 2*4], edx
  mov DWORD [rsp + 3*4], CREATE_GC_FLAGS
  mov DWORD [rsp + 4*4], MY_COLOR_RGB
  mov DWORD [rsp + 5*4], 0
  mov DWORD [rsp + 6*4], ecx

  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, CREATE_GC_PACKET_U32_COUNT*4
  syscall

  cmp rax, CREATE_GC_PACKET_U32_COUNT*4
  jnz die

  add rsp, 8*8

  pop rbp
  ret
```

## Creating the window

We can now create the window, which refers to the freshly created graphical context. We also provide the desired x and y coordinates of the window, as well as the desired dimensions (width and height).

Note that those are simply hints and the resulting window may well have different coordinates and dimensions, for example when using a tiling window manager, or when resizing the window.

```x86asm
; Create the X11 window.
; @param rdi The socket file descriptor.
; @param esi The new window id.
; @param edx The window root id.
; @param ecx The root visual id.
; @param r8d Packed x and y.
; @param r9d Packed w and h.
x11_create_window:
static x11_create_window:function
  push rbp
  mov rbp, rsp

  %define X11_OP_REQ_CREATE_WINDOW 0x01
  %define X11_FLAG_WIN_BG_COLOR 0x00000002
  %define X11_EVENT_FLAG_KEY_RELEASE 0x0002
  %define X11_EVENT_FLAG_EXPOSURE 0x8000
  %define X11_FLAG_WIN_EVENT 0x00000800

  %define CREATE_WINDOW_FLAG_COUNT 2
  %define CREATE_WINDOW_PACKET_U32_COUNT (8 + CREATE_WINDOW_FLAG_COUNT)
  %define CREATE_WINDOW_BORDER 1
  %define CREATE_WINDOW_GROUP 1

  sub rsp, 12*8

  mov DWORD [rsp + 0*4], X11_OP_REQ_CREATE_WINDOW | (CREATE_WINDOW_PACKET_U32_COUNT << 16)
  mov DWORD [rsp + 1*4], esi
  mov DWORD [rsp + 2*4], edx
  mov DWORD [rsp + 3*4], r8d
  mov DWORD [rsp + 4*4], r9d
  mov DWORD [rsp + 5*4], CREATE_WINDOW_GROUP | (CREATE_WINDOW_BORDER << 16)
  mov DWORD [rsp + 6*4], ecx
  mov DWORD [rsp + 7*4], X11_FLAG_WIN_BG_COLOR | X11_FLAG_WIN_EVENT
  mov DWORD [rsp + 8*4], 0
  mov DWORD [rsp + 9*4], X11_EVENT_FLAG_KEY_RELEASE | X11_EVENT_FLAG_EXPOSURE


  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, CREATE_WINDOW_PACKET_U32_COUNT*4
  syscall

  cmp rax, CREATE_WINDOW_PACKET_U32_COUNT*4
  jnz die

  add rsp, 12*8

  pop rbp
  ret
```

## Mapping the window

If you are following along at home, and just ran the program, you have realized nothing is displayed.

That is because X11 does not show the window until we have mapped it. This is a simple message to send:

```x86asm
; Map a X11 window.
; @param rdi The socket file descriptor.
; @param esi The window id.
x11_map_window:
static x11_map_window:function
  push rbp
  mov rbp, rsp

  sub rsp, 16

  %define X11_OP_REQ_MAP_WINDOW 0x08
  mov DWORD [rsp + 0*4], X11_OP_REQ_MAP_WINDOW | (2<<16)
  mov DWORD [rsp + 1*4], esi

  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 2*4
  syscall

  cmp rax, 2*4
  jnz die

  add rsp, 16

  pop rbp
  ret
```

We now have a black window:

![Black window](https://gaultier.github.io/blog/x11_x64_black_window.png)

Yay!

## Polling for server messages

We would like to draw text in the window now, but we have to wait for the `Expose` event to be sent to us, which means that the window is visible, to be able to start drawing on it.

We want to listen for all server messages actually, be it errors or events, for example when the user presses a key on the keyboard.

If we do a simple blocking `read(2)`, but the server sends nothing, the program will appear not responding. Not good. The solution is to use the `poll(2)` system call to be awoken by the operating system whenever there is data to be read on the socket, a la NodeJS or Nginx.

First, we need to mark the socket as ‘non-blocking’ since it is by default in blocking mode:

```x86asm
; Set a file descriptor in non-blocking mode.
; @param rdi The file descriptor.
set_fd_non_blocking:
static set_fd_non_blocking:function
  push rbp
  mov rbp, rsp

  mov rax, SYSCALL_FCNTL
  mov rdi, rdi
  mov rsi, F_GETFL
  mov rdx, 0
  syscall

  cmp rax, 0
  jl die

  ; `or` the current file status flag with O_NONBLOCK.
  mov rdx, rax
  or rdx, O_NONBLOCK

  mov rax, SYSCALL_FCNTL
  mov rdi, rdi
  mov rsi, F_SETFL
  mov rdx, rdx
  syscall

  cmp rax, 0
  jl die

  pop rbp
  ret
```

Then, we write a small function to read data on the socket. For simplicity, we only read 32 bytes of data, because most messages from X11 are of this size. We also return the first byte which contains the event type.

```x86asm
; Read the X11 server reply.
; @return The message code in al.
x11_read_reply:
static x11_read_reply:function
  push rbp
  mov rbp, rsp

  sub rsp, 32

  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 32
  syscall

  cmp rax, 1
  jle die

  mov al, BYTE [rsp]

  add rsp, 32

  pop rbp
  ret
```

We now can poll. If an error occurs or the other side has closed their end of the socket, we exit the program.

```x86asm
; Poll indefinitely messages from the X11 server with poll(2).
; @param rdi The socket file descriptor.
; @param esi The window id.
; @param edx The gc id.
poll_messages:
static poll_messages:function
  push rbp
  mov rbp, rsp

  sub rsp, 32

  %define POLLIN 0x001
  %define POLLPRI 0x002
  %define POLLOUT 0x004
  %define POLLERR  0x008
  %define POLLHUP  0x010
  %define POLLNVAL 0x020

  mov DWORD [rsp + 0*4], edi
  mov DWORD [rsp + 1*4], POLLIN

  mov DWORD [rsp + 16], esi ; window id
  mov DWORD [rsp + 20], edx ; gc id

  .loop:
    mov rax, SYSCALL_POLL
    lea rdi, [rsp]
    mov rsi, 1
    mov rdx, -1
    syscall

    cmp rax, 0
    jle die

    cmp DWORD [rsp + 2*4], POLLERR
    je die

    cmp DWORD [rsp + 2*4], POLLHUP
    je die

    mov rdi, [rsp + 0*4]
    call x11_read_reply

    jmp .loop

  add rsp, 16
  pop rbp
  ret
```

## Drawing text

At last, we can draw text. The small difficulty here is that the text is of unknown length in the general case, so we have to compute the size of the X11 message, including the padding at the end. So far, we only had messages of fixed size.

The official documentation has formulas to compute those values.

```x86asm
; Draw text in a X11 window with server-side text rendering.
; @param rdi The socket file descriptor.
; @param rsi The text string.
; @param edx The text string length in bytes.
; @param ecx The window id.
; @param r8d The gc id.
; @param r9d Packed x and y.
x11_draw_text:
static x11_draw_text:function
  push rbp
  mov rbp, rsp

  sub rsp, 1024

  mov DWORD [rsp + 1*4], ecx ; Store the window id directly in the packet data on the stack.
  mov DWORD [rsp + 2*4], r8d ; Store the gc id directly in the packet data on the stack.
  mov DWORD [rsp + 3*4], r9d ; Store x, y directly in the packet data on the stack.

  mov r8d, edx ; Store the string length in r8 since edx will be overwritten next.
  mov QWORD [rsp + 1024 - 8], rdi ; Store the socket file descriptor on the stack to free the register.

  ; Compute padding and packet u32 count with division and modulo 4.
  mov eax, edx ; Put dividend in eax.
  mov ecx, 4 ; Put divisor in ecx.
  cdq ; Sign extend.
  idiv ecx ; Compute eax / ecx, and put the remainder (i.e. modulo) in edx.
  ; LLVM optimizer magic: `(4-x)%4 == -x & 3`, for some reason.
  neg edx
  and edx, 3
  mov r9d, edx ; Store padding in r9.

  mov eax, r8d
  add eax, r9d
  shr eax, 2 ; Compute: eax /= 4
  add eax, 4 ; eax now contains the packet u32 count.


  %define X11_OP_REQ_IMAGE_TEXT8 0x4c
  mov DWORD [rsp + 0*4], r8d
  shl DWORD [rsp + 0*4], 8
  or DWORD [rsp + 0*4], X11_OP_REQ_IMAGE_TEXT8
  mov ecx, eax
  shl ecx, 16
  or [rsp + 0*4], ecx

  ; Copy the text string into the packet data on the stack.
  mov rsi, rsi ; Source string in rsi.
  lea rdi, [rsp + 4*4] ; Destination
  cld ; Move forward
  mov ecx, r8d ; String length.
  rep movsb ; Copy.

  mov rdx, rax ; packet u32 count
  imul rdx, 4
  mov rax, SYSCALL_WRITE
  mov rdi, QWORD [rsp + 1024 - 8] ; fd
  lea rsi, [rsp]
  syscall

  cmp rax, rdx
  jnz die

  add rsp, 1024

  pop rbp
  ret
```

We then call this function inside the polling loop, and we store the ‘exposed’ state in a boolean on the stack to know whether we should render the text or not:

```x86asm
    %define X11_EVENT_EXPOSURE 0xc
    cmp eax, X11_EVENT_EXPOSURE
    jnz .received_other_event

    .received_exposed_event:
    mov BYTE [rsp + 24], 1 ; Mark as exposed.

    .received_other_event:

    cmp BYTE [rsp + 24], 1 ; exposed?
    jnz .loop

    .draw_text:
      mov rdi, [rsp + 0*4] ; socket fd
      lea rsi, [hello_world] ; string
      mov edx, 13 ; length
      mov ecx, [rsp + 16] ; window id
      mov r8d, [rsp + 20] ; gc id
      mov r9d, 100 ; x
      shl r9d, 16
      or r9d, 100 ; y
      call x11_draw_text
```

Finally, we see our `Hello, world!` text displayed inside the window:

![Result](https://gaultier.github.io/blog/x11_x64_final.png)

## The end

Wow, that was a lot. But we did it! We wrote a (albeit simplistic) GUI program in pure assembly, no dependencies, and that’s just 600 lines of code in the end.

How did we fare on the executable size part?

- With debug information: 10744 bytes (10 KiB)
- Without debug information (stripped): 8592 bytes (8 KiB)
- Stripped and OMAGIC (`--omagic` linker flag, from the man page: `Set the text and data sections to be readable and writable. Also, do not page-align the data segment`): 1776 bytes (1 KiB)

Not too shaby, a GUI program in 1 KiB.

Where to go from there?

- We could move text rendering client-side. Doing it server-side has lots of limitations.
- We could add shape rendering, such as quads and circles
- We could listen to keyboard and mouse events (the polling loop is easy to extend to do that)

I hope that you had as much fun as I did!

## Addendum: the full code

```x86asm
; Build with: nasm -f elf64 -g main.nasm && ld main.o -static -o main

BITS 64 ; 64 bits.
CPU X64 ; Target the x86_64 family of CPUs.

section .rodata

sun_path: db "/tmp/.X11-unix/X0", 0
static sun_path:data

hello_world: db "Hello, world!"
static hello_world:data

section .data

id: dd 0
static id:data

id_base: dd 0
static id_base:data

id_mask: dd 0
static id_mask:data

root_visual_id: dd 0
static root_visual_id:data


section .text

%define AF_UNIX 1
%define SOCK_STREAM 1

%define SYSCALL_READ 0
%define SYSCALL_WRITE 1
%define SYSCALL_POLL 7
%define SYSCALL_SOCKET 41
%define SYSCALL_CONNECT 42
%define SYSCALL_EXIT 60
%define SYSCALL_FCNTL 72

; Create a UNIX domain socket and connect to the X11 server.
; @returns The socket file descriptor.
x11_connect_to_server:
static x11_connect_to_server:function
  push rbp
  mov rbp, rsp

  ; Open a Unix socket: socket(2).
  mov rax, SYSCALL_SOCKET
  mov rdi, AF_UNIX ; Unix socket.
  mov rsi, SOCK_STREAM ; Tcp-like.
  mov rdx, 0 ; Automatic protocol.
  syscall

  cmp rax, 0
  jle die

  mov rdi, rax ; Store socket fd in `rdi` for the remainder of the function.

  sub rsp, 112 ; Store struct sockaddr_un on the stack.

  mov WORD [rsp], AF_UNIX ; Set sockaddr_un.sun_family to AF_UNIX
  ; Fill sockaddr_un.sun_path with: "/tmp/.X11-unix/X0".
  lea rsi, sun_path
  mov r12, rdi ; Save the socket file descriptor in `rdi` in `r12`.
  lea rdi, [rsp + 2]
  cld ; Move forward
  mov ecx, 19 ; Length is 19 with the null terminator.
  rep movsb ; Copy.

  ; Connect to the server: connect(2).
  mov rax, SYSCALL_CONNECT
  mov rdi, r12
  lea rsi, [rsp]
  %define SIZEOF_SOCKADDR_UN 2+108
  mov rdx, SIZEOF_SOCKADDR_UN
  syscall

  cmp rax, 0
  jne die

  mov rax, rdi ; Return the socket fd.

  add rsp, 112
  pop rbp
  ret

; Send the handshake to the X11 server and read the returned system information.
; @param rdi The socket file descriptor
; @returns The window root id (uint32_t) in rax.
x11_send_handshake:
static x11_send_handshake:function
  push rbp
  mov rbp, rsp

  sub rsp, 1<<15
  mov BYTE [rsp + 0], 'l' ; Set order to 'l'.
  mov WORD [rsp + 2], 11 ; Set major version to 11.

  ; Send the handshake to the server: write(2).
  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 12*8
  syscall

  cmp rax, 12*8 ; Check that all bytes were written.
  jnz die

  ; Read the server response: read(2).
  ; Use the stack for the read buffer.
  ; The X11 server first replies with 8 bytes. Once these are read, it replies with a much bigger message.
  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 8
  syscall

  cmp rax, 8 ; Check that the server replied with 8 bytes.
  jnz die

  cmp BYTE [rsp], 1 ; Check that the server sent 'success' (first byte is 1).
  jnz die

  ; Read the rest of the server response: read(2).
  ; Use the stack for the read buffer.
  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 1<<15
  syscall

  cmp rax, 0 ; Check that the server replied with something.
  jle die

  ; Set id_base globally.
  mov edx, DWORD [rsp + 4]
  mov DWORD [id_base], edx

  ; Set id_mask globally.
  mov edx, DWORD [rsp + 8]
  mov DWORD [id_mask], edx

  ; Read the information we need, skip over the rest.
  lea rdi, [rsp] ; Pointer that will skip over some data.

  mov cx, WORD [rsp + 16] ; Vendor length (v).
  movzx rcx, cx

  mov al, BYTE [rsp + 21]; Number of formats (n).
  movzx rax, al ; Fill the rest of the register with zeroes to avoid garbage values.
  imul rax, 8 ; sizeof(format) == 8

  add rdi, 32 ; Skip the connection setup
  add rdi, rcx ; Skip over the vendor information (v).
  add rdi, rax ; Skip over the format information (n*8).

  mov eax, DWORD [rdi] ; Store (and return) the window root id.

  ; Set the root_visual_id globally.
  mov edx, DWORD [rdi + 32]
  mov DWORD [root_visual_id], edx

  add rsp, 1<<15
  pop rbp
  ret

; Increment the global id.
; @return The new id.
x11_next_id:
static x11_next_id:function
  push rbp
  mov rbp, rsp

  mov eax, DWORD [id] ; Load global id.

  mov edi, DWORD [id_base] ; Load global id_base.
  mov edx, DWORD [id_mask] ; Load global id_mask.

  ; Return: id_mask & (id) | id_base
  and eax, edx
  or eax, edi

  add DWORD [id], 1 ; Increment id.

  pop rbp
  ret

; Open the font on the server side.
; @param rdi The socket file descriptor.
; @param esi The font id.
x11_open_font:
static x11_open_font:function
  push rbp
  mov rbp, rsp

  %define OPEN_FONT_NAME_BYTE_COUNT 5
  %define OPEN_FONT_PADDING ((4 - (OPEN_FONT_NAME_BYTE_COUNT % 4)) % 4)
  %define OPEN_FONT_PACKET_U32_COUNT (3 + (OPEN_FONT_NAME_BYTE_COUNT + OPEN_FONT_PADDING) / 4)
  %define X11_OP_REQ_OPEN_FONT 0x2d

  sub rsp, 6*8
  mov DWORD [rsp + 0*4], X11_OP_REQ_OPEN_FONT | (OPEN_FONT_NAME_BYTE_COUNT << 16)
  mov DWORD [rsp + 1*4], esi
  mov DWORD [rsp + 2*4], OPEN_FONT_NAME_BYTE_COUNT
  mov BYTE [rsp + 3*4 + 0], 'f'
  mov BYTE [rsp + 3*4 + 1], 'i'
  mov BYTE [rsp + 3*4 + 2], 'x'
  mov BYTE [rsp + 3*4 + 3], 'e'
  mov BYTE [rsp + 3*4 + 4], 'd'


  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, OPEN_FONT_PACKET_U32_COUNT*4
  syscall

  cmp rax, OPEN_FONT_PACKET_U32_COUNT*4
  jnz die

  add rsp, 6*8

  pop rbp
  ret

; Create a X11 graphical context.
; @param rdi The socket file descriptor.
; @param esi The graphical context id.
; @param edx The window root id.
; @param ecx The font id.
x11_create_gc:
static x11_create_gc:function
  push rbp
  mov rbp, rsp

  sub rsp, 8*8

%define X11_OP_REQ_CREATE_GC 0x37
%define X11_FLAG_GC_BG 0x00000004
%define X11_FLAG_GC_FG 0x00000008
%define X11_FLAG_GC_FONT 0x00004000
%define X11_FLAG_GC_EXPOSE 0x00010000

%define CREATE_GC_FLAGS X11_FLAG_GC_BG | X11_FLAG_GC_FG | X11_FLAG_GC_FONT
%define CREATE_GC_PACKET_FLAG_COUNT 3
%define CREATE_GC_PACKET_U32_COUNT (4 + CREATE_GC_PACKET_FLAG_COUNT)
%define MY_COLOR_RGB 0x0000ffff

  mov DWORD [rsp + 0*4], X11_OP_REQ_CREATE_GC | (CREATE_GC_PACKET_U32_COUNT<<16)
  mov DWORD [rsp + 1*4], esi
  mov DWORD [rsp + 2*4], edx
  mov DWORD [rsp + 3*4], CREATE_GC_FLAGS
  mov DWORD [rsp + 4*4], MY_COLOR_RGB
  mov DWORD [rsp + 5*4], 0
  mov DWORD [rsp + 6*4], ecx

  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, CREATE_GC_PACKET_U32_COUNT*4
  syscall

  cmp rax, CREATE_GC_PACKET_U32_COUNT*4
  jnz die

  add rsp, 8*8

  pop rbp
  ret

; Create the X11 window.
; @param rdi The socket file descriptor.
; @param esi The new window id.
; @param edx The window root id.
; @param ecx The root visual id.
; @param r8d Packed x and y.
; @param r9d Packed w and h.
x11_create_window:
static x11_create_window:function
  push rbp
  mov rbp, rsp

  %define X11_OP_REQ_CREATE_WINDOW 0x01
  %define X11_FLAG_WIN_BG_COLOR 0x00000002
  %define X11_EVENT_FLAG_KEY_RELEASE 0x0002
  %define X11_EVENT_FLAG_EXPOSURE 0x8000
  %define X11_FLAG_WIN_EVENT 0x00000800

  %define CREATE_WINDOW_FLAG_COUNT 2
  %define CREATE_WINDOW_PACKET_U32_COUNT (8 + CREATE_WINDOW_FLAG_COUNT)
  %define CREATE_WINDOW_BORDER 1
  %define CREATE_WINDOW_GROUP 1

  sub rsp, 12*8

  mov DWORD [rsp + 0*4], X11_OP_REQ_CREATE_WINDOW | (CREATE_WINDOW_PACKET_U32_COUNT << 16)
  mov DWORD [rsp + 1*4], esi
  mov DWORD [rsp + 2*4], edx
  mov DWORD [rsp + 3*4], r8d
  mov DWORD [rsp + 4*4], r9d
  mov DWORD [rsp + 5*4], CREATE_WINDOW_GROUP | (CREATE_WINDOW_BORDER << 16)
  mov DWORD [rsp + 6*4], ecx
  mov DWORD [rsp + 7*4], X11_FLAG_WIN_BG_COLOR | X11_FLAG_WIN_EVENT
  mov DWORD [rsp + 8*4], 0
  mov DWORD [rsp + 9*4], X11_EVENT_FLAG_KEY_RELEASE | X11_EVENT_FLAG_EXPOSURE


  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, CREATE_WINDOW_PACKET_U32_COUNT*4
  syscall

  cmp rax, CREATE_WINDOW_PACKET_U32_COUNT*4
  jnz die

  add rsp, 12*8

  pop rbp
  ret

; Map a X11 window.
; @param rdi The socket file descriptor.
; @param esi The window id.
x11_map_window:
static x11_map_window:function
  push rbp
  mov rbp, rsp

  sub rsp, 16

  %define X11_OP_REQ_MAP_WINDOW 0x08
  mov DWORD [rsp + 0*4], X11_OP_REQ_MAP_WINDOW | (2<<16)
  mov DWORD [rsp + 1*4], esi

  mov rax, SYSCALL_WRITE
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 2*4
  syscall

  cmp rax, 2*4
  jnz die

  add rsp, 16

  pop rbp
  ret

; Read the X11 server reply.
; @return The message code in al.
x11_read_reply:
static x11_read_reply:function
  push rbp
  mov rbp, rsp

  sub rsp, 32

  mov rax, SYSCALL_READ
  mov rdi, rdi
  lea rsi, [rsp]
  mov rdx, 32
  syscall

  cmp rax, 1
  jle die

  mov al, BYTE [rsp]

  add rsp, 32

  pop rbp
  ret

die:
  mov rax, SYSCALL_EXIT
  mov rdi, 1
  syscall


; Set a file descriptor in non-blocking mode.
; @param rdi The file descriptor.
set_fd_non_blocking:
static set_fd_non_blocking:function
  push rbp
  mov rbp, rsp

  %define F_GETFL 3
  %define F_SETFL 4

  %define O_NONBLOCK 2048

  mov rax, SYSCALL_FCNTL
  mov rdi, rdi
  mov rsi, F_GETFL
  mov rdx, 0
  syscall

  cmp rax, 0
  jl die

  ; `or` the current file status flag with O_NONBLOCK.
  mov rdx, rax
  or rdx, O_NONBLOCK

  mov rax, SYSCALL_FCNTL
  mov rdi, rdi
  mov rsi, F_SETFL
  mov rdx, rdx
  syscall

  cmp rax, 0
  jl die

  pop rbp
  ret

; Poll indefinitely messages from the X11 server with poll(2).
; @param rdi The socket file descriptor.
; @param esi The window id.
; @param edx The gc id.
poll_messages:
static poll_messages:function
  push rbp
  mov rbp, rsp

  sub rsp, 32

  %define POLLIN 0x001
  %define POLLPRI 0x002
  %define POLLOUT 0x004
  %define POLLERR  0x008
  %define POLLHUP  0x010
  %define POLLNVAL 0x020

  mov DWORD [rsp + 0*4], edi
  mov DWORD [rsp + 1*4], POLLIN

  mov DWORD [rsp + 16], esi ; window id
  mov DWORD [rsp + 20], edx ; gc id
  mov BYTE [rsp + 24], 0 ; exposed? (boolean)

  .loop:
    mov rax, SYSCALL_POLL
    lea rdi, [rsp]
    mov rsi, 1
    mov rdx, -1
    syscall

    cmp rax, 0
    jle die

    cmp DWORD [rsp + 2*4], POLLERR
    je die

    cmp DWORD [rsp + 2*4], POLLHUP
    je die

    mov rdi, [rsp + 0*4]
    call x11_read_reply

    %define X11_EVENT_EXPOSURE 0xc
    cmp eax, X11_EVENT_EXPOSURE
    jnz .received_other_event

    .received_exposed_event:
    mov BYTE [rsp + 24], 1 ; Mark as exposed.

    .received_other_event:

    cmp BYTE [rsp + 24], 1 ; exposed?
    jnz .loop

    .draw_text:
      mov rdi, [rsp + 0*4] ; socket fd
      lea rsi, [hello_world] ; string
      mov edx, 13 ; length
      mov ecx, [rsp + 16] ; window id
      mov r8d, [rsp + 20] ; gc id
      mov r9d, 100 ; x
      shl r9d, 16
      or r9d, 100 ; y
      call x11_draw_text


    jmp .loop


  add rsp, 16
  pop rbp
  ret

; Draw text in a X11 window with server-side text rendering.
; @param rdi The socket file descriptor.
; @param rsi The text string.
; @param edx The text string length in bytes.
; @param ecx The window id.
; @param r8d The gc id.
; @param r9d Packed x and y.
x11_draw_text:
static x11_draw_text:function
  push rbp
  mov rbp, rsp

  sub rsp, 1024

  mov DWORD [rsp + 1*4], ecx ; Store the window id directly in the packet data on the stack.
  mov DWORD [rsp + 2*4], r8d ; Store the gc id directly in the packet data on the stack.
  mov DWORD [rsp + 3*4], r9d ; Store x, y directly in the packet data on the stack.

  mov r8d, edx ; Store the string length in r8 since edx will be overwritten next.
  mov QWORD [rsp + 1024 - 8], rdi ; Store the socket file descriptor on the stack to free the register.

  ; Compute padding and packet u32 count with division and modulo 4.
  mov eax, edx ; Put dividend in eax.
  mov ecx, 4 ; Put divisor in ecx.
  cdq ; Sign extend.
  idiv ecx ; Compute eax / ecx, and put the remainder (i.e. modulo) in edx.
  ; LLVM optimizer magic: `(4-x)%4 == -x & 3`, for some reason.
  neg edx
  and edx, 3
  mov r9d, edx ; Store padding in r9.

  mov eax, r8d
  add eax, r9d
  shr eax, 2 ; Compute: eax /= 4
  add eax, 4 ; eax now contains the packet u32 count.


  %define X11_OP_REQ_IMAGE_TEXT8 0x4c
  mov DWORD [rsp + 0*4], r8d
  shl DWORD [rsp + 0*4], 8
  or DWORD [rsp + 0*4], X11_OP_REQ_IMAGE_TEXT8
  mov ecx, eax
  shl ecx, 16
  or [rsp + 0*4], ecx

  ; Copy the text string into the packet data on the stack.
  mov rsi, rsi ; Source string in rsi.
  lea rdi, [rsp + 4*4] ; Destination
  cld ; Move forward
  mov ecx, r8d ; String length.
  rep movsb ; Copy.

  mov rdx, rax ; packet u32 count
  imul rdx, 4
  mov rax, SYSCALL_WRITE
  mov rdi, QWORD [rsp + 1024 - 8] ; fd
  lea rsi, [rsp]
  syscall

  cmp rax, rdx
  jnz die

  add rsp, 1024

  pop rbp
  ret

_start:
global _start:function
  call x11_connect_to_server
  mov r15, rax ; Store the socket file descriptor in r15.

  mov rdi, rax
  call x11_send_handshake

  mov r12d, eax ; Store the window root id in r12.

  call x11_next_id
  mov r13d, eax ; Store the gc_id in r13.

  call x11_next_id
  mov r14d, eax ; Store the font_id in r14.

  mov rdi, r15
  mov esi, r14d
  call x11_open_font


  mov rdi, r15
  mov esi, r13d
  mov edx, r12d
  mov ecx, r14d
  call x11_create_gc

  call x11_next_id

  mov ebx, eax ; Store the window id in ebx.

  mov rdi, r15 ; socket fd
  mov esi, eax
  mov edx, r12d
  mov ecx, [root_visual_id]
  mov r8d, 200 | (200 << 16) ; x and y are 200
  %define WINDOW_W 800
  %define WINDOW_H 600
  mov r9d, WINDOW_W | (WINDOW_H << 16)
  call x11_create_window

  mov rdi, r15 ; socket fd
  mov esi, ebx
  call x11_map_window

  mov rdi, r15 ; socket fd
  call set_fd_non_blocking

  mov rdi, r15 ; socket fd
  mov esi, ebx ; window id
  mov edx, r13d ; gc id
  call poll_messages

  ; The end.
  mov rax, SYSCALL_EXIT
  mov rdi, 0
  syscall
```
