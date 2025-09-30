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

        // Arquivos
        String maquinaFile = args[0];
        String entradaFile = args[1];
        String saidaFile = "saida.txt";

        // Carrega máquina
        String jsonContent = Files.readString(Path.of(maquinaFile));
        JSONObject maquina = new JSONObject(jsonContent);

        int estado = maquina.getInt("initial");
        JSONArray finaisArr = maquina.getJSONArray("final");
        Set<Integer> finais = new HashSet<>();
        for (int i = 0; i < finaisArr.length(); i++) {
            finais.add(finaisArr.getInt(i));
        }
        String branco = maquina.getString("white");

        // Indexa transições em HashMap (rápido)
        Map<Integer, Map<String, Transicao>> transicoes = new HashMap<>();
        JSONArray trans = maquina.getJSONArray("transitions");
        for (int i = 0; i < trans.length(); i++) {
            Transicao t = new Transicao(trans.getJSONObject(i));
            transicoes.putIfAbsent(t.from, new HashMap<>());
            transicoes.get(t.from).put(t.read, t);
        }

        // Fita inicial -> HashMap (só guarda posições usadas)
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

        // Salvar fita final (apenas do menor ao maior índice tocado)
        int min = fita.keySet().stream().min(Integer::compareTo).orElse(0);
        int max = fita.keySet().stream().max(Integer::compareTo).orElse(0);

        StringBuilder sb = new StringBuilder();
        for (int i = min; i <= max; i++) {
            sb.append(fita.getOrDefault(i, branco));
        }
        Files.writeString(Path.of(saidaFile), sb.toString());

        // imprime resultado
        System.out.println(aceita ? 1 : 0);
    }
}
