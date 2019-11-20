package consistency.formulaParser;
/*
Language
--------

The input format has the following syntax in BNF:
( [ ... ] means optional,  { ... } means repeated arbitrary many times)

   expr ::= iff
   iff ::= implies { '<->' implies }
   implies ::= or [ '->' or | '<-' or ]
   or ::= and { '|' and }
   and ::= not { '&' not }
   not ::= basic | '!' not
   basic ::= var | '(' expr ')'

and 'var' is a string over letters, digits and the following characters:

  - _ . [ ] $ @

The last character of 'var' should be different from '-'.
 */

public class Parser {
    // 1. replace a(x) with a_x_
    // 2. keep track of all variables and values
    // 3. for each formula get cnf
    // 4. update that cnf with appropriate values
    // 5. add to model description



}
