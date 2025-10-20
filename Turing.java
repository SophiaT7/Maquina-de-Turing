import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

class Transicao {
    int from;
    int to;
    String read;
    String write;
    String dir;


    //org.json para ler diretamente do arquivo json e criar objetos transicao automaticamente
    public Transicao(JSONObject obj) {
        this.from = obj.getInt("from");
        this.to = obj.getInt("to");
        this.read = obj.getString("read");
        this.write = obj.getString("write");
        this.dir = obj.getString("dir");
    }
}

public class Turing {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: java Turing maquina.json entrada.txt");
            return;
        }

        // arquivos
        String maquinaFile = args[0];
        String entradaFile = args[1];
        String saidaFile = "saida.txt";

        // le json da maquina e carrega os parametro-- estado inicial, finais, simbolo branco e as transicoesd
        String jsonContent = Files.readString(Path.of(maquinaFile));
        JSONObject maquina = new JSONObject(jsonContent);

        int estado = maquina.getInt("initial");
        JSONArray finaisArr = maquina.getJSONArray("final");
        Set<Integer> finais = new HashSet<>();
        for (int i = 0; i < finaisArr.length(); i++) {
            finais.add(finaisArr.getInt(i));
        }
        String branco = maquina.getString("white");

        // uso um hashmap dentro de outro hashmap para acessar as transicoes de forma rápida
        Map<Integer, Map<String, Transicao>> transicoes = new HashMap<>();
        JSONArray trans = maquina.getJSONArray("transitions");
        for (int i = 0; i < trans.length(); i++) {
            Transicao t = new Transicao(trans.getJSONObject(i));
            transicoes.putIfAbsent(t.from, new HashMap<>());
            transicoes.get(t.from).put(t.read, t);
        }//em vez de procurar a transição ideal percorrendo uma lista, eu acesso diretamente(estado, simbolo)

        // fita eh outro hashmap, n precisa criar vetor enorme apenas as possicoes q sao usadas sao armazenadas
        String entrada = Files.readString(Path.of(entradaFile)).trim();
        Map<Integer, String> fita = new HashMap<>();
        for (int i = 0; i < entrada.length(); i++) {
            fita.put(i, String.valueOf(entrada.charAt(i)));
        }

        int cabeca = 0;
        boolean aceita = false;
        int maxSteps = 10_000_000; // limite p/ evitar loop infinito

        for (int step = 0; step < maxSteps; step++) {
            String simbolo = fita.getOrDefault(cabeca, branco);
            //lew o simbolo atual, aplica a transição correspondente, escreve na fita, move a cabeça e muda de estado
            Transicao t = transicoes
                .getOrDefault(estado, Collections.emptyMap())
                .get(simbolo);

            if (t == null) break; // sem transição aplicável

            // aplica
            fita.put(cabeca, t.write);
            estado = t.to;

            if (t.dir.equals("R")) {
                cabeca++;
            } else if (t.dir.equals("L")) {
                cabeca--;
            }

            if (finais.contains(estado)) {
                aceita = true;
                break;
            }
        }

        // aq ele ve quem eh o maior e mnor valor
        // como a fita eh um hashmap, as posicoes so existem qnd acessadas, assim ele precisa saber o maior valor (fim) o primeiro valor(inicio)
        // mais rapido pois ele escreve so qnd for relevante
        int min = fita.keySet().stream().min(Integer::compareTo).orElse(0);
        int max = fita.keySet().stream().max(Integer::compareTo).orElse(0);

        StringBuilder sb = new StringBuilder();
        for (int i = min; i <= max; i++) {
            sb.append(fita.getOrDefault(i, branco));
        }
        //vai gravar conteudo numa fita e arq saida.txt 1(aceita) ou 0(rejeita)
        Files.writeString(Path.of(saidaFile), sb.toString());

        // imprime resultado
        System.out.println(aceita ? 1 : 0);
    }
}
