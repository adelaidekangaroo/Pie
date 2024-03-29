## `Pie` - compiler for a simple pseudo language. 

        pseudo code: 
                a := x and (0x09 xor z or 0xAF); #mycode
        
        asm code:
                MOV AX, 0x09
                MOV BX, z
                XOR AX, BX
        
                MOV BX, 0xAF
                OR AX, BX
        
                MOVE BX, AX
                MOV AX, x
                AND AX, BX
        
                MOV a, AX

##### _How to start?_

    Pie.compile(String filePath, boolean showTree)

`Needed JDK 12 or upper`

The language has several rules:
1. Contains boolean expressions separated by ';' (point with
   comma).
2. Boolean expressions are composed of identifiers, hexadecimal numbers,
   assignment signs (: =), or, xor, and, not operation signs, and round brackets.
3. Used comment type - #

`Pie` has three parts:

1. Lexical analyzer
2. Syntactical analyzer
3. Object Code Generator and Optimizer (Assembler)

## _Lexical analyzer_

      The lexical analyzer is based on a finite - state machine:

      G = ({N, S, C, I, A, Z, X, H, E}, {0...9, a...F,(,), ′ # ′ , ; , =, :}, 𝛿, N, {S})
      N - start state
      S - end state
      C - comment input state
      I - identifier input state
      A - assignment statement input state
      Z - 0 input state
      X - x input state
      H - hexadecimal input state
      E - error state (entered an invalid character)

![](docs/img/finite_state_machine.jpg)
<details>
   <summary><a>Test</a></summary>
   <p>
      <b>---- <tt>Input</tt> ----</b><br/><br/>
      a := x and (0x09 xor z or 0xAF); <br/><br/>
      <b>---- <tt>Debugging</tt> ----</b><br/><br/>
      currentStateN a <br/>
      currentStateI <br/>
      currentStateN : <br/>
      currentStateA = <br/>
      currentStateN <br/>
      currentStateN x <br/>
      currentStateI <br/>
      currentStateN a <br/>
      currentStateI n <br/>
      currentStateI d <br/>
      currentStateI <br/>
      currentStateN ( <br/>
      currentStateN 0 <br/>
      currentStateZ x <br/>
      currentStateX 0 <br/>
      currentStateH 9 <br/>
      currentStateH <br/>
      currentStateN x <br/>
      currentStateI o <br/>
      currentStateI r <br/>
      currentStateI <br/>
      currentStateN z <br/>
      currentStateI <br/>
      currentStateN o <br/>
      currentStateI r <br/>
      currentStateI <br/>
      currentStateN 0 <br/>
      currentStateZ x <br/>
      currentStateX A <br/>
      currentStateH F <br/>
      currentStateH ) <br/>
      currentStateN ; <br/><br/>
      <b>---- <tt>Token table</tt> ----</b><br/><br/>
   <table>
      <tr>
         <td align="right">ID</td>
         <td align="left">a</td>
      </tr>
      <tr>
         <td align="right">ASSIGNMENT</td>
         <td align="left">:=</td>
      </tr>
      <tr>
         <td align="right">ID</td>
         <td align="left">x</td>
      </tr>
      <tr>
         <td align="right">KEYWORD</td>
         <td align="left">and</td>
      </tr>
      <tr>
         <td align="right">BRACE</td>
         <td align="left">(</td>
      </tr>
      <tr>
         <td align="right">HEX</td>
         <td align="left">0x09</td>
      </tr>
      <tr>
         <td align="right">KEYWORD</td>
         <td align="left">xor</td>
      </tr>
      <tr>
         <td align="right">ID</td>
         <td align="left">z</td>
      </tr>
      <tr>
         <td align="right">KEYWORD</td>
         <td align="left">or</td>
      </tr>
      <tr>
         <td align="right">HEX</td>
         <td align="left">0xAF</td>
      </tr>
      <tr>
         <td align="right">BRACE</td>
         <td align="left">)</td>
      </tr>
      <tr>
         <td align="right">END_STATEMENT</td>
         <td align="left">;</td>
      </tr>
   </table>
   </p> 
</details>

## _Syntactical analyzer_

1. There are preset language rules:

       S -> a := F; (Rule 1)    
       F -> F or T | F xor T | T (Rules 2,3,4)   
       T -> T and E | E (Rules 5,6)  	
       E -> (F) | not (F) | a (Rules 7,8,9)  
       G({S,F,T,E},{a, := , ; , or, xor, and, not, (, )},P,S)  

2. The set of right and left symbols:

![](docs/img/set_right_and_left_symbols.jpg)

3. The precedence table:

![](docs/img/precedence_table.jpg)

4. Minimizing rules:   

       E -> a := E; (Rule 1)   
       E -> E or E | E xor E | E (Rules 2,3,4)   
       E -> E and E | E (Rules 5,6)  
       E -> (E) | not (E) | a (Rules 7,8,9)

<details> 
    <summary><a>Test</a></summary>
    <p>
    <b>---- <tt>Input</tt> ----</b><br/><br/>
    a := x and (0x09 xor z or 0xAF);<br/><br/>
    <b>---- <tt>Building the output tree</tt> ----</b></p>
 
![](docs/img/output_tree.jpg) 
        
 <p>
<b>---- <tt>Debugging</tt> ----</b><br/><br/>
Line - [a := a and ( a xor a or a ) ;]<br/> 
Memory - []<br/>
Action - Transfer

Line - [:= a and ( a xor a or a ) ;]<br/> 
Memory - [a]<br/>
Compare... a = :=
Action - Transfer

Line - [a and ( a xor a or a ) ;]<br/> 
Memory - [a :=]<br/>
Compare... := < a
Action - Transfer

Line - [and ( a xor a or a ) ;]<br/> 
Memory - [a := a]<br/>
Compare... a > and
Action - Convolution 9

Line - [and ( a xor a or a ) ;]<br/>   
Memory - [a := E]<br/>
Compare... := < and
Action - Transfer

Line - [( a xor a or a ) ;]<br/>   
Memory - [a := E and]<br/>
Compare... and < (
Action - Transfer

Line - [a xor a or a ) ;]<br/>   
Memory - [a := E and (]<br/>
Compare... ( < a
Action - Transfer

Line - [xor a or a ) ;]<br/>   
Memory - [a := E and ( a]<br/>
Compare... a > xor
Action - Convolution 9

Line - [xor a or a ) ;]<br/>   
Memory - [a := E and ( E]<br/>
Compare... ( < xor
Action - Transfer

Line - [a or a ) ;]<br/>   
Memory - [a := E and ( E xor]<br/>
Compare... xor < a
Action - Transfer

Line - [or a ) ;]<br/>   
Memory - [a := E and ( E xor a]<br/>
Compare... a > or
Action - Convolution 9

Line - [or a ) ;]<br/>   
Memory - [a := E and ( E xor E]<br/>
Compare... xor > or
Action - Convolution 3

Line - [or a ) ;]<br/>   
Memory - [a := E and ( E]<br/>
Compare... ( < or
Action - Transfer

Line - [a ) ;]<br/>   
Memory - [a := E and ( E or]<br/>
Compare... or < a
Action - Transfer

Line - [) ;]<br/>   
Memory - [a := E and ( E or a]<br/>
Compare... a > )
Action - Convolution 9

Line - [) ;]<br/>   
Memory - [a := E and ( E or E]<br/>
Compare... or > )
Action - Convolution 2

Line - [) ;]<br/>   
Memory - [a := E and ( E]<br/>
Compare... ( = )
Action - Transfer

Line - [;]<br/>   
Memory - [a := E and ( E )]<br/>
Compare... ) > ;
Action - Convolution 7

Line - [;]<br/>   
Memory - [a := E and E]<br/>
Compare... and > ;
Action - Convolution 5

Line - [;]<br/>   
Memory - [a := E]<br/>
Compare... := = ;
Action - Transfer

Line - []<br/>   
Memory - [a := E ;]<br/>
Action - Convolution 1

Line - []<br/>   
Memory - [E]<br/>
</p>
</details>

## _Object Code Generator and Optimizer_

Generation based on triads.

<details> 
    <summary><a>Test</a></summary>
<p>
Triads:<br/>
     
  1: xor (0x09, z)<br/>
  2: or (^1, 0xAF)<br/>
  3: and (x, ^2)<br/>
  4: := (a, ^3)<br/>
  
Code:<br/>

MOV AX, 0x09<br/>
MOV BX, z<br/>
XOR AX, BX<br/>
PUSH AX<br/>

POP AX<br/>
MOV BX, 0xAF<br/>
OR AX, BX<br/>
PUSH AX<br/>

POP BX<br/>
MOV AX, x<br/>
AND AX, BX<br/>
PUSH AX<br/>

POP AX<br/>
MOV a, AX<br/>
      
Collapsing triads:<br/>
        
Step 1:<br/>
        
1: xor (0x09, z)<br/>
2: or (^1, 0xAF)<br/>
3: and (x, ^2)<br/>
4: := (a, ^3)<br/>
        
Step 2:<br/>
        
1: xor (0x09, z)<br/>
2: or (^1, 0xAF)<br/>
3: and (x, ^2)<br/>
4: := (a, ^3)<br/>
        
Optimized code:<br/>
        
MOV AX, 0x09<br/>
MOV BX, z<br/>
XOR AX, BX<br/>
        
MOV BX, 0xAF<br/>
OR AX, BX<br/>
        
MOVE BX, AX<br/>
MOV AX, x<br/>
AND AX, BX<br/>
        
MOV a, AX<br/>
</p>
</details>
