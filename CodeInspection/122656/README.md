# Inspeção de Código — Aluno 122656

## Projeto
BattleShip2

## Requisito desenvolvido
Move Timer — temporizador de jogadas.

Ficheiro analisado: src/main/java/battleship/Tasks.java

Commit do requisito: 3ef2de5

## 1. Inspeção IntelliJ IDEA

Foi realizada uma inspeção estática de todo o projeto através da opção Code > Inspect Code, utilizando o perfil Project Default.

Resultados:
- 7 erros
- 713 warnings
- 667 weak warnings
- 137 warnings na categoria Java
- 15 warnings na categoria Security

Entre os problemas identificados destacam-se avisos relacionados com redundância de código, possíveis bugs e dependências potencialmente vulneráveis.

Evidência: intellij-inspection.png

## 2. Análise Qodana

Foi executada uma análise local do projeto utilizando o Qodana.

Foram identificados 24 problemas de criticidade alta:

- Performance: 2
- Probable bugs: 6
- Security: 15
- Threading issues: 1

A análise permitiu identificar potenciais problemas de qualidade e segurança do código.

Evidência: qodana-analysis.png

## 3. Análise do requisito Move Timer

Foi analisado o código desenvolvido para o requisito Move Timer, localizado na classe Tasks.java.

Identificou-se uma possível limitação na medição do tempo das jogadas: o temporizador mede o tempo de introdução do comando, mas não necessariamente a duração completa da jogada.

Este problema foi documentado na Issue #3:

https://github.com/LETI-122656/Battleship2/issues/3

## 4. Conclusão

A inspeção estática permitiu identificar problemas relacionados com manutenção, possíveis bugs e segurança.

O IntelliJ IDEA apresentou uma análise abrangente do projeto, enquanto o Qodana destacou 24 problemas de criticidade alta.

A análise manual do requisito Move Timer permitiu identificar uma limitação funcional que não foi diretamente assinalada pelo Qodana.

Os resultados demonstram a importância de combinar ferramentas automáticas com inspeção manual do código.
