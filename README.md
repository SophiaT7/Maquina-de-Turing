# Simulador de Máquina de Turing

Este projeto implementa um **simulador de Máquina de Turing** capaz de realizar operações de **Duplo Balanceamento** e **Verificação de Igualdade**. Ele é otimizado para lidar com **arquivos grandes** usando estruturas eficientes de armazenamento como **HashMap**.

---

## Funcionalidades

- **Duplo Balanceamento**: Verifica se cadeias possuem pares correspondentes de caracteres (por exemplo, parênteses, colchetes) e se estão balanceadas de forma dupla.
- **Verificação de Igualdade**: Confere se duas cadeias ou sequências são iguais, mesmo em arquivos muito grandes.
- **Leitura de Arquivos Grandes**: Utiliza `HashMap` para armazenar estados e transições, permitindo processar arquivos extensos sem problemas de memória.
- **Simulação Passo a Passo**: Mostra o progresso da máquina de Turing durante a execução das operações.
- **Configuração por Arquivo JSON**: Estados, transições e regras da máquina são definidos em arquivos JSON separados, facilitando manutenção e expansão.

---

## Como o HashMap Melhora a Performance

O **HashMap** é usado para armazenar rapidamente:

1. **Transições da máquina**: Cada par `(estado, símbolo)` é usado como **chave**, e a ação correspondente (`próximo estado`, `símbolo a escrever`, `direção`) é o **valor**.  
   - Isso permite que, ao ler cada símbolo da fita, o simulador **acesse diretamente a transição correta** sem percorrer todas as opções.  
   - Acesso típico: **O(1)** (quase instantâneo), mesmo com milhares de estados ou símbolos.

2. **Estados visitados ou resultados intermediários** (opcional): Para grandes arquivos, o HashMap ajuda a **evitar recalcular trechos da fita**, economizando tempo e memória.

**Benefício principal:** Com o HashMap, o simulador consegue processar **arquivos enormes** de entrada sem travar, mantendo a execução rápida e escalável.

---

## Estrutura do Projeto

