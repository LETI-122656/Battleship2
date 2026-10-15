# Treino do protocolo com IA

Nesta parte treinei o LLM para conseguir comunicar com o jogo Battleship2 através de JSON.

O LLM enviava uma rajada com 3 tiros e eu colocava esses tiros no programa. Depois copiava o JSON gerado pelo programa e enviava-o de volta ao LLM.

## Exemplo de uma rajada

O LLM enviou:

```json
{
  "shots": [
    {"row": "B", "column": 3},
    {"row": "E", "column": 7},
    {"row": "H", "column": 9}
  ]
}
```

O programa respondeu:

```json
{
  "validShots": 3,
  "sunkBoats": [
    {
      "count": 1,
      "type": "Barca"
    }
  ],
  "repeatedShots": 0,
  "outsideShots": 0,
  "hitsOnBoats": [],
  "missedShots": 2
}
```

Continuei a fazer várias rajadas para testar situações diferentes, como tiros repetidos, tiros fora do tabuleiro, tiros na água e tiros em barcos.

No final do treino o LLM respondeu:

```json
{
  "protocolLearned": true
}
```

Assim ficou concluído o treino do protocolo de comunicação entre o LLM e o Battleship2.