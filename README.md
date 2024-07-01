# Syntax_analyzer
1. Ejercicio. Crear un analizador Sintáctico (Cualquier tipo, LL, LR o sin usar estos métodos) en el Lenguaje de programación de su preferencia.
  * Netbeans 8.2
  * Java JDK 8 2.

2. Pruebas de código para realizar en el REDACTOR SEMANTICO
int main() {  
  int anthony = 0808;  
  for (int i = 0; i < 10; i++) {
    int numeroAleatorio = random.nextInt(101); // 101 es exclusivo, genera números entre 0 y 100
    System.out.println("Número aleatorio " + (i + 1) + ": " + numeroAleatorio);
  }
}

3. Documentar Lenguaje utilizado del analizador Léxico para realizar pruebas.
  ```java
  // Inicialización de variables
        ambitoActual = "";
        erroresSemanticos = "";
        variablesDeclaradas.clear();
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        boolean seEncontraronErrores = false; // Variable para controlar si se encontraron errores
        int linea = 1; // Contador de línea
        String ambitoActual = "otro"; // Ámbito actual (main o otro)

        // Análisis léxico y semántico
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                break;
            }

            switch (token) {
                case Linea:
                    linea++;
                    break;
                case T_dato:
                    String tipoDato = lexer.lexeme;
                    Tokens nextToken = lexer.yylex();
                    if (nextToken == Tokens.Identificador) {
                        String identificador = lexer.lexeme;
                        // Verificar si la variable se declaro dentro de main o no
                        if (ambitoActual.equals("main")) {
                            //validar si la variable se ha declarado mas de una vez
                            if (variablesDeclaradas.containsKey(identificador)) {
                            erroresSemanticos += " Error: La variable '" + identificador + "' ya ha sido declarada en el mismo ámbito (línea " + linea + ")\n";
                            seEncontraronErrores = true;
                        } else {
                            variablesDeclaradas.put(identificador, tipoDato);
                            // Verificar si hay un valor asignado a la variable y que este sea el corecto
                            Tokens tokenAsignacion = lexer.yylex();
                            if (tokenAsignacion == Tokens.Igual) {
                                Tokens tipoValor = lexer.yylex();
                                if ((tipoDato.equals("int") && tipoValor == Tokens.NumeroDecimal) ||
                                    (tipoDato.equals("float") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal) || 
                                        (tipoDato.equals("double") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal)){
                                    erroresSemanticos += " Error: Tipo de dato incorrecto para la variable '" + identificador + "' (línea " + linea + ")\n";
                                    seEncontraronErrores = true;
                                }
                            }
                        }
                        } else {
                            erroresSemanticos += " Error: Variable '" + identificador + "' declarada fuera de 'main' (línea " + linea + ")\n";
                            seEncontraronErrores = true;
                        }
                    }
                    break;
                case Identificador:
                    String identificador = lexer.lexeme;
                    // Verificar si la variable no ha sido declarada
                    if (!variablesDeclaradas.containsKey(identificador)) {
                        erroresSemanticos += " Error: Variable no declarada '" + identificador + "' (línea " + linea + ")\n";
                        seEncontraronErrores = true;
                    } else {
                        Tokens tokenAsignacion = lexer.yylex();
                        // Verificar si hay un valor asignado a la variable y que este sea el corecto
                        if (tokenAsignacion == Tokens.Igual) {
                            String tipoDatoVariable = variablesDeclaradas.get(identificador);
                            Tokens tipoValor = lexer.yylex();
                            if ((tipoDatoVariable.equals("int") && tipoValor == Tokens.NumeroDecimal) ||
                                (tipoDatoVariable.equals("float") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal) ||
                                (tipoDatoVariable.equals("double") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal)) {
                                erroresSemanticos += " Error: Tipo de dato incorrecto para la variable '" + identificador + "' (línea " + linea + ")\n";
                                seEncontraronErrores = true;
                            }
                        }
                    }
                    break;
                case Llave_a:
                    ambitoActual = "main";
                    break;
                case Llave_c:
                    ambitoActual = "otro";
                    break;
                default:
                    break;
            }
        }

        // Verificación final de errores y mensajes
         if (!seEncontraronErrores) {
            erroresSemanticos += "No hay errores semánticos. \n";
        }

         // Mostrar los errores semánticos
        txtAnalizarSem.setText(erroresSemanticos);
    }

    
    private void analizarSintactico(){
        // TODO add your handling code here:
        String ST = txtResultado.getText();
        Sintax s = new Sintax(new code.LexerCup(new StringReader(ST)));
        
        try {
            s.parse();
            txtAnalizarSin.setText("Analisis realizado correctamente");
            txtAnalizarSin.setForeground(new Color(25, 111, 61));
        } catch (Exception ex) {
            Symbol sym = s.getS();
            txtAnalizarSin.setText("Error de sintaxis. Linea: " + (sym.right + 1) + " Columna: " + (sym.left + 1) + ", Texto: \"" + sym.value + "\"");
            txtAnalizarSin.setForeground(Color.red);
        }
  ```

4.Imagen de portada.

![Imagen Previsualizacion](imageSemanticAnalyzer.png)



