package lab4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyLab
{
    //*
    private class MyLang
    {
        private int axiom;
        private boolean create;
        private LinkedList<Node> language;
        private LinkedList<TableNode> lexemsTable;
        private int[] terminals;
        private int[] nonTerminals;
        private Map<Integer, Set<Node>> NonTermRulesMap;
        private int[] epsilonTerminals;

        public MyLang(String fileLang) {
            this.create = false;
            this.language = new LinkedList<>();
            this.lexemsTable = new LinkedList<>();
            this.readGrammar(fileLang);
            this.NonTermRulesMap = new HashMap<>();
            if (this.create) {
                Iterator iterator = this.language.iterator();
                if (iterator.hasNext()) this.axiom = ((Node)iterator.next()).getRule()[0];

                this.terminals = this.createTerminals();
                this.nonTerminals = this.createNonterminals();
                for (int nonterm: nonTerminals)
                    this.NonTermRulesMap.put(nonterm, new HashSet<Node>());
                for (Node prod: language)
                    this.NonTermRulesMap.get(prod.getRule()[0]).add(prod);
            }
        }

        public boolean isCreate() {
            return this.create;
        }

        private int[] createTerminals() {
            int count = 0;
            Iterator i$ = this.lexemsTable.iterator();

            TableNode tmp;
            while(i$.hasNext()) {
                tmp = (TableNode)i$.next();
                if(tmp.getLexemCode() > 0) ++count;
            }

            int[] terminal = new int[count];
            count = 0;
            i$ = this.lexemsTable.iterator();

            while(i$.hasNext()) {
                tmp = (TableNode)i$.next();
                if(tmp.getLexemCode() > 0) {
                    terminal[count] = tmp.getLexemCode();
                    ++count;
                }
            }

            return terminal;
        }

        private int[] createNonterminals() {
            int count = 0;
            Iterator i$ = this.lexemsTable.iterator();

            TableNode tmp;
            while(i$.hasNext()) {
                tmp = (TableNode)i$.next();
                if(tmp.getLexemCode() < 0) ++count;
            }

            int[] nonTerminal = new int[count];
            count = 0;
            i$ = this.lexemsTable.iterator();

            while(i$.hasNext()) {
                tmp = (TableNode)i$.next();
                if(tmp.getLexemCode() < 0) {
                    nonTerminal[count] = tmp.getLexemCode();
                    ++count;
                }
            }

            return nonTerminal;
        }

        private LlkContext[] createTerminalLang() {
            LlkContext[] cont = new LlkContext[this.terminals.length];

            for (int ii = 0; ii < this.terminals.length; ++ii) {
                int[] trmWord = new int[]{this.terminals[ii]};
                cont[ii] = new LlkContext();
                cont[ii].addWord(trmWord);
            }

            return cont;
        }

        String getLexemText(int code)
        {
            Iterator i$ = this.lexemsTable.iterator();

            TableNode tmp;
            do {
                if (!i$.hasNext()) return null;

                tmp = (TableNode)i$.next();
            } while (tmp.getLexemCode() != code);

            return tmp.getLexemText();
        }

        private int getLexemCode(String lexem, int lexemClass) {
            Iterator i$ = this.lexemsTable.iterator();

            TableNode tmp;
            do {
                if (!i$.hasNext()) return 0;

                tmp = (TableNode)i$.next();
            } while (!tmp.getLexemText().equals(lexem) || (tmp.getLexemCode() & -16777216) != lexemClass);

            return tmp.getLexemCode();
        }

        /*
        int getLexemCode(byte[] lexem, int lexemLen)
        {
            Iterator i$ = this.lexemsTable.iterator();

            String ss;
            int ii;
            TableNode tmp;
            do {
                do {
                    if (!i$.hasNext()) return -1;

                    tmp = (TableNode)i$.next();
                    ss = tmp.getLexemText();
                } while (ss.length() != lexemLen);

                ii = 0;
                while (ii < ss.length() && ss.charAt(ii) == (char)lexem[ii]) ++ii;
            } while (ii != ss.length());

            return tmp.getLexemCode();
        }
        */

        private void readGrammar(String filename) {
            char[] lexem = new char[180];
            int[] rule = new int[80];

            BufferedReader s;
            try {
                s = new BufferedReader(new FileReader(filename.trim()));
            } catch (FileNotFoundException var24) {
                System.out.print("Файл:" + filename.trim() + " не відкрито\n");
                this.create = false;
                return;
            }

            for (int i = 0; i < lexem.length; ++i) lexem[i] = 0;

            int[] var27 = new int[80];

            int line;
            for (line = 0; line < var27.length; ++line) var27[line] = 0;

            int pos = 0;
            byte q = 0;
            int posRule = 0;
            line = 0;
            String readline = null;
            int readPos = 0;
            int readlen = 0;

            try {
                int newLexemCode;
                TableNode nodeTmp;
                Node nod;
                while (s.ready()) {
                    if (readline == null || readPos >= readlen) {
                        readline = s.readLine();
                        if (line == 0 && readline.charAt(0) == '\ufeff') readline = readline.substring(1);

                        readlen = readline.length();
                        ++line;
                    }

                    for (readPos = 0; readPos < readlen; ++readPos) {
                        char letter = readline.charAt(readPos);
                        String e;
                        boolean strTmp;
                        Iterator iterator;
                        TableNode iterator1;
                        switch (q) {
                            case 0:
                                if (letter == 32 || letter == 9 || letter == 13 || letter == 10 || letter == 8) break;

                                if (readPos == 0 && posRule > 0 && (letter == 33 || letter == 35)) {
                                    nod = new Node(rule, posRule);
                                    this.language.add(nod);
                                    if (letter == 33) {
                                        posRule = 1;
                                        break;
                                    }

                                    posRule = 0;
                                }

                                byte var26 = 0;
                                pos = var26 + 1;
                                lexem[var26] = letter;
                                if (letter == 35) q = 1;
                                else if (letter == 92) {
                                    --pos;
                                    q = 3;
                                } else q = 2;
                                break;
                            case 1:
                                lexem[pos++] = letter;
                                if(letter != 35 && readPos != 0) break;

                                e = new String(lexem, 0, pos);
                                nodeTmp = new TableNode(e, -2147483648);
                                strTmp = true;
                                iterator = this.lexemsTable.iterator();

                                while (iterator.hasNext()) {
                                    iterator1 = (TableNode)iterator.next();
                                    if (nodeTmp.equals(iterator1)) {
                                        strTmp = false;
                                        break;
                                    }
                                }

                                if (strTmp) this.lexemsTable.add(nodeTmp);

                                newLexemCode = this.getLexemCode(e, -2147483648);
                                rule[posRule++] = newLexemCode;
                                pos = 0;
                                q = 0;
                                break;
                            case 2:
                                if(letter == 92) {
                                    --pos;
                                    q = 3;
                                } else {
                                    if(letter != 32 && readPos != 0 && letter != 35 && letter != 13 && letter != 9) {
                                        lexem[pos++] = letter;
                                        continue;
                                    }

                                    e = new String(lexem, 0, pos);
                                    nodeTmp = new TableNode(e, 268435456);
                                    strTmp = true;
                                    iterator = this.lexemsTable.iterator();

                                    while(iterator.hasNext()) {
                                        iterator1 = (TableNode)iterator.next();
                                        if(nodeTmp.equals(iterator1)) {
                                            strTmp = false;
                                            break;
                                        }
                                    }

                                    if(strTmp) this.lexemsTable.add(nodeTmp);

                                    newLexemCode = this.getLexemCode(e, 268435456);
                                    rule[posRule++] = newLexemCode;
                                    pos = 0;
                                    q = 0;
                                    --readPos;
                                }
                                break;
                            case 3:
                                lexem[pos++] = letter;
                                q = 2;
                        }
                    }
                }

                if (pos != 0) {
                    int var29;
                    if (q == 1) var29 = -2147483648;
                    else var29 = 268435456;

                    String var31 = new String(lexem, 0, pos);
                    nodeTmp = new TableNode(var31, var29);
                    boolean var30 = true;

                    for (TableNode tmp : this.lexemsTable)
                        if (nodeTmp.equals(tmp)) {
                            var30 = false;
                            break;
                        }

                    if (var30) this.lexemsTable.add(nodeTmp);

                    newLexemCode = this.getLexemCode(var31, var29);
                    rule[posRule++] = newLexemCode;
                }

                if (posRule > 0) {
                    nod = new Node(rule, posRule);
                    this.language.add(nod);
                }

                this.create = true;
            } catch (IOException var25) {
                System.out.println(var25.toString());
                this.create = false;
            }
        }


        int[] getTerminals() {
            return this.terminals;
        }

        int[] getNonTerminals() {
            return this.nonTerminals;
        }

        private Set<Integer> createEpsNonTerms()
        {
            Set<Integer> epsNonTerms = new HashSet<>();
            Set<Integer> otherNonTerms = new HashSet<>();
            Set<Integer> nontermsToAdd;
            for (int nonterm: this.nonTerminals)
            {
                boolean add = false;
                Node prod;
                Iterator rule_iter = this.NonTermRulesMap.get(nonterm).iterator();
                while (rule_iter.hasNext() & !add)
                {
                    prod = (Node)rule_iter.next();
                    if (prod.getlen() == 1)
                    {
                        add = true;
                        epsNonTerms.add(nonterm);
                    }
                    else otherNonTerms.add(nonterm);
                }
            }
            boolean flag = true;
            while (flag)
            {
                nontermsToAdd = new HashSet<>();
                Set<Integer> new_otherNonTerms = new HashSet<>(otherNonTerms);
                Set<Integer> new_epsNonTerms = new HashSet<>(epsNonTerms);
                for (int nonterm: otherNonTerms)
                {
                    boolean add = false;
                    Node prod;
                    Iterator rule_iter = this.NonTermRulesMap.get(nonterm).iterator();
                    while (rule_iter.hasNext() & !add)
                    {
                        prod = (Node)rule_iter.next();
                        int[] rule = prod.getRule();
                        boolean nice_rule = true;
                        for (int i = 1; i < prod.getlen() & nice_rule; ++i)
                            if (!epsNonTerms.contains(rule[i])) nice_rule = false;
                        if (nice_rule)
                        {
                            add = true;
                            nontermsToAdd.add(nonterm);
                            new_epsNonTerms.add(nonterm);
                            new_otherNonTerms.remove(nonterm);
                        }
                    }
                }
                epsNonTerms = new_epsNonTerms;
                otherNonTerms = new_otherNonTerms;
                if (nontermsToAdd.isEmpty()) flag = false;
            }
            return epsNonTerms;
        }

        private void printEpsNonTerms()
        {
            Set<Integer> epsNonTerms = this.createEpsNonTerms();
            if (epsNonTerms.isEmpty())
            {
                System.out.println(" е-Нетермінали відсутні в даній граматиці.");
                return;
            }
            StringBuilder out = new StringBuilder(" Список е-Нетерміналів:\n ");
            for (int eps: epsNonTerms)
                out.append(this.getLexemText(eps) + " ");
            System.out.println(out.toString());
        }
    }

    public void main_body_5()
    {
        String filename = "gram.txt";
        MyLang testLang = new MyLang (filename);
        if (!testLang.isCreate())
        {
            System.out.println (" Граматика непрочитана");
            return;
        }
        System.out.println (" Граматика прочитана успішно");

        testLang.printEpsNonTerms();
    }
}
