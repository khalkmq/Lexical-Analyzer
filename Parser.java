public class Parser
{
    public static final int ENDMARKER   =  0;
    public static final int LEXERROR    =  1;

    public static final int PRINT       = 11; 
    public static final int BOOL        = 12; 
    public static final int INT         = 13;
    public static final int FLOAT       = 14;
    public static final int RECORD      = 15;
    public static final int SIZE        = 17;
    public static final int NEW         = 18;
    public static final int WHILE       = 19;
    public static final int IF          = 20; //if
    public static final int THEN        = 21; 
    public static final int ELSE        = 22;
    public static final int RETURN      = 23;
    public static final int BREAK       = 24;
    public static final int CONTINUE    = 25;
    public static final int AND         = 27; //&&
    public static final int OR          = 28; //||
    public static final int NOT         = 29; //!
    public static final int BEGIN       = 30; //{
    public static final int END         = 31; //}
    public static final int ADDR        = 32; //&
    public static final int LPAREN      = 33; //(
    public static final int RPAREN      = 34; //)
    public static final int LBRACKET    = 35; //[
    public static final int RBRACKET    = 37; //]
    public static final int ASSIGN      = 38; //=
    public static final int GT          = 39; //>=
    public static final int LT          = 40; //<=
    public static final int PLUS        = 41; //+
    public static final int MINUS       = 42; //-
    public static final int MUL         = 43; //*
    public static final int DIV         = 44; // /
    public static final int MOD         = 45; //%
    public static final int SEMI        = 47; //;
    public static final int COMMA       = 48; //,
    public static final int DOT         = 49; //.
    public static final int EQ          = 50; //==
    public static final int NE          = 51; //!=
    public static final int LE          = 52; //
    public static final int GE          = 53;
    public static final int BOOL_LIT    = 54;
    public static final int INT_LIT     = 55;
    public static final int FLOAT_LIT   = 57;
    public static final int IDENT       = 58;
	
    public static Object rtnToken;

    public static boolean Debuging = false;
    public static boolean errorOverride = true;
    public static int lineCount = 1;
    
    //public static boolean backTrack = false;
    public static int line_number=0;
 
    public class Token {
        public int       type;
        public ParserVal attr;
        public Token(int type, ParserVal attr) {
            this.type   = type;
            this.attr   = attr;
        }
    }

    public ParserVal yylval;
    Token _token;
    Lexer _lexer;
    public Parser(java.io.Reader r) throws java.io.IOException
    {
        _lexer = new Lexer(r, this);
        _token = null;
        Advance();
    }
    

    public void Advance() throws java.io.IOException
    {
        int token_type = _lexer.yylex();
	line_number = lineCount;
	lineCount = _lexer.lineno;
    	if (Debuging) System.out.println(lineCount);

        if(token_type ==  0)      _token = new Token(ENDMARKER , null  );
        else if(token_type == -1) _token = new Token(LEXERROR  , yylval);
        else                      _token = new Token(token_type, yylval);
    }


    public boolean Match(int token_type) throws java.io.IOException
    {
        boolean match = (token_type == _token.type);
	rtnToken = yylval.obj;
        if(_token.type != ENDMARKER)	 Advance();
        return match;
    }


    public int yyparse() throws java.io.IOException, Exception
    {
        parse();
        return 0;
    }


    /** Print specific errors **/
    public void printError(String val, Object i) throws java.io.IOException, Exception
    {
        errorOverride=false;
	System.out.println("Syntax error: " + val +" is expected instead of \""+ i +"\" at line "+line_number+".");
    }


    /** Print parser message **/
    public void parse() throws java.io.IOException, Exception
    {
        boolean successparse = program();
        if(successparse)System.out.println("Success: no syntax error found.");
	
        else{
		if(errorOverride)System.out.println("Syntax error: There is a syntax error at line "+lineCount+".");		
		System.out.println("Error: there exists syntax error(s).");
	}
    }


    /** ****************************** GRAMMAR STARTS HERE ****************************** **/
    
    /** ####################################################### **/    
    /** ###########          (1) program          ########### **/
    public boolean program() throws java.io.IOException, Exception
    {	
	if(Debuging) System.out.println("program");
        switch(_token.type)
        {
            	// program -> decl_list
            	case INT:
			if( decl_list()      == false) return false;
			return true;

		// program -> epsilon
            	case ENDMARKER:
            		return true;
        }
        return false;
    }


    /** ####################################################### **/
    /** ###########          (2) decl_list          ########### **/
    public boolean decl_list() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("decl_list");
        switch(_token.type)
        {
            	// decl_list -> decl_list'
            	case INT:
            		if( decl_list_() == false) return false;
            		return true;

		// decl_list -> epsilon
		case ENDMARKER:
			return true;
        }
        return false;
    }
    

    /** ####################################################### **/
    /** ###########          (3) type_spec          ########### **/
    public boolean type_spec() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("type_spec");
        switch(_token.type)
        {
            	// type_spec -> "int"
            	case INT:
                	if( Match(INT) == false) {printError("\"int\"", rtnToken ); return false;}
                	return true;
        }
        return false;
    }


    /** ####################################################### **/
    /** ###########          (4) fun_decl           ########### **/
    public boolean fun_decl() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("fun_decl");
        switch(_token.type)
        {
        	// fun_decl -> type_spec IDENT "(" params ")" compound_stmt
        	case INT:
                	if( type_spec()     == false) return false;
                	if( Match(IDENT)    == false) {printError("an identifier", rtnToken ); return false;}
                	if( Match(LPAREN)   == false) {printError("\"(\"", rtnToken ); return false;}
                	if( params()        == false) return false;                	
			if( Match(RPAREN)   == false) {printError("\")\"", rtnToken ); return false;}
                	if( compound_stmt() == false) return false;
                	return true;
        }
        return false;
    }

	
    /** ####################################################### **/
    /** ###########           (5) params            ########### **/
    public boolean params() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("params");
    	switch (_token.type) 
	{
		//params -> param_list
     		case INT:
        		if (param_list() == false) return false;
        		return true;
     		
		//params -> epsilon
		case RPAREN:
        		return true;
    	}
    	return false;							
    }


    /** ####################################################### **/
    /** ###########          (6) param_list         ########### **/
    public boolean param_list() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("param_list");
        switch(_token.type)
	{
		// param_list-> param param_list_
		case INT:
        		if (param() 	  == false) return false;
			if (param_list_() == false) return false;
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########            (7) param            ########### **/
    public boolean param() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("param");
        switch(_token.type)
	{
		//param -> type_spec IDENT
		case INT:
        		if (type_spec()  == false) return false;
        		if (Match(IDENT) == false) {printError("an identifier", rtnToken ); return false;}
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########          (8) stmt_list          ########### **/
    public boolean stmt_list() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("stmt_list");
        switch(_token.type)
	{
		// stmt_list -> stmt_list'
		case IDENT:
		case SEMI:
		case IF:
		case BEGIN:
		case WHILE:
			if(stmt_list_() == false) return false;
			return true;

		// stmt_list -> epsilon
		case END:
			return true;
		
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########            (9) stmt             ########### **/
    public boolean stmt() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("stmt");
	// stmt -> expr_stmt | compound_stmt | if_stmt | while_stmt
        switch(_token.type)
	{	
		// stmt -> expr_stmt
		case IDENT:
		case SEMI:
			if(expr_stmt()     == false) return false;
			return true;

		// stmt -> compound_stmt
		case BEGIN:		
			if(compound_stmt() == false) return false;
			return true;

		// stmt -> if_stmt
		case IF:	
			if(if_stmt()       == false) return false;
			return true;	

		// stmt -> while_stmt
		case WHILE:
			if(while_stmt()    == false) return false;
			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########          (10) expr_stmt         ########### **/
    public boolean expr_stmt() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("expr_stmt");
	// expr_stmt -> IDENT "=" expr ";"        | ";" 
        switch(_token.type)
	{	
		// expr_stmt -> IDENT
		case IDENT:
			if( Match(IDENT)      == false) {printError("an identifier", rtnToken ); return false;}
                	if( Match(ASSIGN)     == false) {printError("\"=\"", rtnToken ); return false;}
                	if( expr() 	      == false) return false;
                	if( Match(SEMI)       == false) {printError("\";\"", rtnToken ); return false;}
			return true;

		// expr_stmt -> ";"
		case SEMI:
                	if( Match(SEMI)       == false) {printError("\";\"", rtnToken ); return false;}
			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########         (11) while_stmt         ########### **/
    public boolean while_stmt() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("while_stmt");
        switch(_token.type)
	{
		// while -> "while" "("  expr  ")"  stmt
		case WHILE:
			if( Match(WHILE)   == false) {printError("\"while\"", rtnToken ); return false;}
			if( Match(LPAREN)  == false) {printError("\"(\"", rtnToken ); return false;}
			if( expr()         == false) return false;
			if( Match(RPAREN)  == false) {printError("\")\"", rtnToken ); return false;}
			if( stmt()         == false) return false;
			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########       (12) compound_stmt        ########### **/
    public boolean compound_stmt() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("compound_stmt");
        switch(_token.type)
	{
		// compound_stmt -> "{" local_decls stmt_list "}" 
		case BEGIN:
			if( Match(BEGIN)  == false) {printError("\"{\"", rtnToken ); return false;}
			if( local_decls() == false) return false;
			if( stmt_list()   == false) return false;
			if( Match(END)    == false) {printError("\"}\"", rtnToken ); return false;}
			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########         (13) local_decls        ########### **/
    public boolean local_decls() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("local_decls");
        switch(_token.type)
	{
		// local_decls-> local_decls'
		case INT:
    			if (local_decls_() == false) return false;
    			return true;

		// local_decls -> epsilon		
		case IDENT:
 		case SEMI:
 		case BEGIN:
		case END:
 		case IF:
		case WHILE:
			return true;
		
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########        (14) local_decl          ########### **/
    public boolean local_decl() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("local_decl");
        switch(_token.type)
	{
		//local_decl-> type_spec IDENT ";" 
                case INT:
                	if( type_spec()  == false) return false;
			if( Match(IDENT) == false) {printError("an identifier", rtnToken ); return false;}	
                	if( Match(SEMI)  == false) {printError("\";\"", rtnToken ); return false;}
			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########          (15) if_stmt           ########### **/
    public boolean if_stmt() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("if_stmt");
        switch(_token.type)
	{
		// if_stmt-> "if" "(" expr ")"  stmt "else" stmt 
		case IF:
			if( Match(IF)     == false) {printError("\"if\"", rtnToken ); return false;}
			if( Match(LPAREN) == false) {printError("\"(\"", rtnToken ); return false;}
			if( expr()        == false) return false;
			if( Match(RPAREN) == false) {printError("\")\"", rtnToken ); return false;}
			if( stmt()        == false) return false;
			if( Match(ELSE)   == false) {printError("\"else\"", rtnToken ); return false;}
			if( stmt()        == false) return false;
			return true;  
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########         (16) arg_list           ########### **/
    public boolean arg_list() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("arg_list");
        switch(_token.type)
	{
		//arg_list -> expr arg_list'
		case IDENT:
		case LPAREN:
		case INT_LIT:
        		if (expr()      == false) return false;
        		if (arg_list_() == false) return false;
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########            (17) args            ########### **/
    public boolean args() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("args");
        switch(_token.type)
	{
		// args-> expr arg_list' 
		case IDENT:
		case LPAREN:
		case INT_LIT:
        		if (expr()      == false) return false;
        		if (arg_list_() == false) return false;
        		return true;

		// args -> epsilon
     		case RPAREN:
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########            (18) expr            ########### **/
    public boolean expr() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("expr");
        switch(_token.type)
	{
		// expr -> term expr
		case IDENT:
		case LPAREN:
		case INT_LIT:    	
			if (term()   == false) return false;
    			if (expr_()  == false) return false;
    			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########            (19) term            ########### **/
    public boolean term() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("term");
        switch(_token.type)
	{
		//term -> factor term'
		case IDENT:
		case LPAREN:
		case INT_LIT:    	
			if (factor()  == false) return false;
    			if (term_()   == false) return false;
    			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########           (20) factor           ########### **/
    public boolean factor() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("factor");
        switch(_token.type)
	{
		// factor -> IDENT factor'   
     		case IDENT:
        		if (Match(IDENT) == false) {printError("an identifier", rtnToken ); return false;}
        		if (factor_()    == false) return false;
        		return true;

		// factor -> "("   expr ")" 
     		case LPAREN:
        		if (Match(LPAREN)  == false) {printError("\"(\"", rtnToken ); return false;}		
        		if (expr()         == false) return false;
        		if (Match(RPAREN)  == false) {printError("\")\"", rtnToken ); return false;}
        		return true;

		// factor -> INT_LIT
     		case INT_LIT:
			if (Match(INT_LIT)== false) return false;
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########         (21) decl_list_         ########### **/
    public boolean decl_list_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("decl_list_");
    	switch (_token.type) 
	{
     		// decl_list'	-> fun_decl decl_list'
     		case INT:
        		if (fun_decl()   == false) return false;
        		if (decl_list_() == false) return false;
        		return true;

     		// decl_list'	-> epsilon
     		case ENDMARKER:
        		return true;
    	}
    	return false; 
    }


    /** ####################################################### **/
    /** ###########         (22) param_list_        ########### **/
    public boolean param_list_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("param_list_");
        switch(_token.type)
	{
		// param_list_ -> "," param param_list'
	     	case COMMA:
        		if (Match(COMMA)  == false) {printError("\",\"", rtnToken ); return false;}
        		if (param()       == false) return false; //int a
        		if (param_list_() == false) return false;
        		return true;
		
		// param_list_ -> epsilon
     		case RPAREN:
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########         (23) stmt_list_         ########### **/
    public boolean stmt_list_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("stmt_list_");
        switch(_token.type)
	{
	    	// stmt_list_ -> stmt  stmt_list'
		case IDENT:
 		case SEMI:
 		case BEGIN:
 		case IF:
		case WHILE:
        		if (stmt()       == false) return false;
        		if (stmt_list_() == false) return false;
        		return true;

	   	// stmt_list_ ->  epsilon
		case END:
			return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########        (24) local_decls_        ########### **/
    public boolean local_decls_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("local_decls_");
        switch(_token.type)
	{
	    	// local_decls_ -> local_decl local_decls_ 
     		case INT:
        		if (local_decl()   == false) return false;
        		if (local_decls_() == false) return false;
        		return true;

		// local_decls_ -> epsilon
		case IDENT:
			return true;
 		case SEMI:
			return true;
 		case BEGIN:
			return true;
 		case IF:
			return true;
		case END:
			return true;
		case WHILE:
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########          (25) arg_list_         ########### **/
    public boolean arg_list_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("arg_list_");
        switch(_token.type)
	{
    		// arg_list' -> "," expr arg_list'
	     	case COMMA:
        		if (Match(COMMA) == false) {printError("\",\"", rtnToken ); return false;}
        		if (expr()       == false) return false;
        		if (arg_list_()  == false) return false;
        		return true;

        	// arg_list' -> epsilon
     		case RPAREN:
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########           (26) expr_            ########### **/
    public boolean expr_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("expr_");
        switch(_token.type)
	{
		// expr_ -> "+" term expr'
		case PLUS:
        		if (Match(PLUS) == false) {printError("\"+\"", rtnToken ); return false;}
        		if (term()      == false) return false;
        		if (expr_()     == false) return false;
        		return true;

		// expr_ -> epsilon
     		case COMMA:
		case SEMI:
		case RPAREN:
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########           (27) term_            ########### **/
    public boolean term_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("term_");
        switch(_token.type)
	{
			// term_ ->  "==" factor term' 
		case EQ:
        		if (Match(EQ) == false) {printError("\"==\"", rtnToken ); return false;}
        		if (factor()  == false) return false;
        		if (term_()   == false) return false;
        		return true;

		// term_ ->  "*" factor term' 
     		case MUL:
        		if (Match(MUL) == false) {printError("\"*\"", rtnToken ); return false;}
        		if (factor()   == false) return false;
        		if (term_()    == false) return false;
        		return true;

		// term_ ->  epsilon
     		case PLUS:
     		case COMMA:
		case SEMI:
		case RPAREN:
        		return true;
	}
	return false;
    }


    /** ####################################################### **/
    /** ###########          (28) factor_           ########### **/
    public boolean factor_() throws java.io.IOException, Exception
    {
	if(Debuging) System.out.println("factor_");
        switch(_token.type)
	{
		// factor_ -> "("args")" 
     		case LPAREN:
        		if (Match(LPAREN) == false) {printError("\"(\"", rtnToken ); return false;}
        		if (args()        == false) return false;
        		if (Match(RPAREN) == false) {printError("\")\"", rtnToken ); return false;}
         		return true;

		// factor_ -> epsilon
     		case EQ:
     		case MUL:
     		case PLUS:
     		case COMMA:
		case RPAREN:
		case SEMI:	
			return true;
	}
	return false;
    }

} // End


