import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

public class Terminal {

    LinkedList<String> afd; //afd completo
    //auxiliares 
    LinkedList<String[]> prog;
    LinkedList<String> aux; //usado na leitura do .txt e no tratamento de strings
    LinkedList<String> states = new LinkedList<>();
    LinkedList<String[]> statesToReplace = new LinkedList<>();
    String oldState1;
    String oldState2;
    String newState;
    //auxiliares modelagem AFD
    LinkedList<String> finalStates = new LinkedList<>();
    String currentState = "q0";

    public Terminal() {
        aux = new LinkedList<>();
        Path path1 = Paths.get("Cafeteira.txt");
        try (BufferedReader reader = Files.newBufferedReader(path1, Charset.defaultCharset())) {
        String line = null;
        while ((line = reader.readLine()) != null) {
            aux.add(line);
        }
        } catch (IOException e) {
            System.err.format("Erro na leitura do arquivo: ", e);        
        }
        createTreatedProgList();
        createAFD();
        printAFD();
    }

    public void menu() {
        Scanner in = new Scanner(System.in);
        System.out.print("Insira uma palavra para testar ou 9 para sair: ");
        String line = in.nextLine();
        
        while(!(line.equals("9"))) {
     
            if (acceptWord(line)) {
                System.out.println("Palavra aceita!");
            } else {
                System.out.println("Palavra rejeitada!");
            }
            System.out.print("Insira uma palavra para testar ou 9 para sair: ");
            line = in.nextLine();
        }
    }

    //modelaçao do afd
    public boolean acceptWord(String word) {
        //tratamentos
        LinkedList<String> finalStates = treatFinalStates();
        setCurrentState(); 
        String splitWord[] = word.split("");
        //fim tratamentos

        for(int i = 0; i < splitWord.length; ++i) {
            if  ( !(acceptSymbol(currentState, splitWord[i])) ) {
                return false;
            }
        }
        if ( finalStates.contains(currentState) ) {
            return true;
        }
        return false;
    }

    public boolean acceptSymbol(String state, String symbol) {
        //iterar sob afd em busca das regras para determinado estado e verificar se simbolo passa na regra
        for(int i = 0; i < prog.size(); ++i) {
            if ( state.equalsIgnoreCase(prog.get(i)[0]) && symbol.equalsIgnoreCase(prog.get(i)[1]) ) {
                currentState = prog.get(i)[2];
                return true;
            }
        }
        return false;
    }

    public void setCurrentState() {
        String[] splitDefinitionAFD = afd.get(0).split("\\{"); //initial state sera [2]
        String[] splitInitialState = splitDefinitionAFD[2].split("\\}"); //usar somente [1]
        splitInitialState[1] = splitInitialState[1].replaceAll(",", ""); //remove virgulas
        this.currentState = splitInitialState[1]; 
    }

    public LinkedList<String> treatFinalStates(){
        //tratamento e formataçao de string 
        LinkedList<String> finalStates = new LinkedList<>();
        String[] splitDefinitionAFD = afd.get(0).split("\\{"); //finalstate sera [3]
        String[] splitFinalStates = splitDefinitionAFD[3].split(",");
        for(int i = 0; i < splitFinalStates.length; ++i) {
            splitFinalStates[i] = splitFinalStates[i].replaceAll(",", "");
            splitFinalStates[i] = splitFinalStates[i].replaceAll("\\}", "");
            splitFinalStates[i] = splitFinalStates[i].replaceAll("\\)", "");
        }
        for(int i = 0; i < splitFinalStates.length; ++i) {
            finalStates.add(splitFinalStates[i]);
        }
        return finalStates;
    }

    public void printAFD(){
        for(String s:afd) {
            System.out.println(s);
        }
    }

    //cria o AFD no formato aceito pela modelagem do AFD
    public void createAFD() {
        afd = new LinkedList<>();
        String[] split1 = aux.get(0).split("}"); //usar somente [0] e [1]
        String[] initialnFinalState = split1[2].split("\\{");
        String[] aux = split1[0].split(",");
        String[] aux1 = aux[0].split("\\{");
        aux[0] = aux1[1];
        for(String s:aux) {
            states.add(s);
        } 
        //tratar states
        for(int i = 0; i < states.size(); ++i) {
            for(int j = 0; j < statesToReplace.size(); ++j) {
                if (states.get(i).equalsIgnoreCase(statesToReplace.get(j)[0]) || states.get(i).equalsIgnoreCase(statesToReplace.get(j)[1])) { 
                    states.set(i, statesToReplace.get(j)[2]);
                    states.remove(i+1);
                }
            }
        }
        //atualizar estados antigos para os novos
        for (int i = 1; i < states.size(); ++i) {
            if (states.get(i).equalsIgnoreCase(oldState1) || states.get(i).equalsIgnoreCase(oldState2) ) {
                //states.get(i) = newState;
                states.set(i, newState);
                int index = i;
                states.remove(index+1);
            }
        }
        //fim verificaçao e tratamento da lista de estados
        
        //inicio montagem da linha primeira linha do afd (definiçao do afd)
        StringBuilder sb = new StringBuilder();
        sb.append("AFD M=({");
        for(int i = 0; i < states.size(); ++i) {
            sb.append("<" + states.get(i) + ">");
            if (i!=states.size()-1) {
                sb.append(",");
            }
        }
        sb.append("}");
        sb.append(split1[1]); //alfabeto aceito
        sb.append("},");
        sb.append(initialnFinalState[0].replace(",", "")); //adiciona estado final
        sb.append(",{");
        sb.append(initialnFinalState[1]);
        sb.append("})");

        afd.add(sb.toString());
        //fim montagem linha identificaçao do automato
        //inicio montagem e inserçao de prog no afd
        afd.add("Prog");
        for(int i = 0; i < prog.size(); ++i) {
            sb = new StringBuilder();
            sb.append("(");
            sb.append(prog.get(i)[0]);
            sb.append(",");
            sb.append(prog.get(i)[1]);
            sb.append(")=");
            sb.append(prog.get(i)[2]);
            afd.add(sb.toString());
        }
    }

    //Cria lista tratada
    public void createTreatedProgList() {
        prog = new LinkedList<>();
        for( int i = 0; i < aux.size(); ++i ) {
            if ( aux.get(i).equalsIgnoreCase("prog") ) {//para se for prog
                for(int j = i+1; j < aux.size(); ++j) { //começa a iterar a partir do index de prog +1

                    //Tratamento e split de cada linha a partir de Prog
                    String[] split = aux.get(j).split(","); //separa na virgula, usar somente o [0]
                    String[] split2 = split[1].split("="); //separa no igual, usar [0] e [1]
                    split[0] = split[0].replace("(", ""); 
                    split2[0] = split2[0].replace(")", "");
                    String[] treatedProgLine = new String[3];
                    treatedProgLine[0] = split[0];
                    treatedProgLine[1] = split2[0];
                    treatedProgLine[2] = split2[1];
                    prog.add(treatedProgLine);           //adiciona a linha tratada ao list prog
                    //Fim burocracia                    
                }
            }
        }
        //verifica se tem indeterminismo
        for( int i = 0; i < prog.size(); ++i) { 
            for(int j = 0; j < prog.size(); ++j) {
                if ( prog.get(i)[0].equalsIgnoreCase(prog.get(j)[0]) && prog.get(i)[1].equalsIgnoreCase(prog.get(j)[1]) && i!=j) {
                    oldState1 = prog.get(i)[2];
                    oldState2 = prog.get(j)[2];
                    if( !(oldState1.equalsIgnoreCase(oldState2)) ) {
                        newState = prog.get(i)[2] + prog.get(j)[2];
                        String[] z = {oldState1,oldState2,newState};
                        statesToReplace.add(z);
                    }
                    else {
                        newState = oldState1;
                    }
                    //NOVO ESTADO COMPOSTO -> onde era qqer dos antigos agora sera o novo
                    for (int k = 0; k < prog.size(); ++k) { //for para substituir pelo novo estado composto onde necessario
                        if ( prog.get(k)[0].equals(oldState1) || prog.get(k)[0].equals(oldState2) && k!=j && k!=i) {
                            prog.get(k)[0] = newState;
                           // prog.remove(j);
                        }
                        else if ( prog.get(k)[2].equals(oldState1) || prog.get(k)[2].equals(oldState2) && k!=j && k!=i) {
                            prog.get(k)[2] = newState;
                            //prog.remove(j);
                        }
                    }
                    prog.remove(j);
                }
            }
        }

        // System.out.println("sdjadsa");
    }
}