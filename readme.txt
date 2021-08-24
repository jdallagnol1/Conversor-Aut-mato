Desenvolvido por João Dall Agnol - Conversor AFND -> AFD

Para escolher o arquivo de entrada deve-se alterar o parametro x em Path.get(x), classe Terminal, linha 28.
Em seguida a main em App pode ser executada, imprimindo no terminal o novo afd e pedindo uma palavra para testa-lo. 
Para encerrar os testes deve-se passar 9 como input, ao inves da palavra a ser testada.

Exemplo de .txt de entrada:
M=({q0,q1,q2,q3,q4,q5,q6,q7,q8},{L,Q,C,E,B,S,P,R,D},q0,{q7}}) 
Prog
(q0,L)=q1
(q1,D)=q0
(q1,E)=q2
(q1,C)=q2
(q1,Q)=q2
(q2,S)=q3
(q2,B)=q3
(q3,P)=q4
(q3,P)=q5
(q4,R)=q6
(q5,R)=q6
(q6,D)=q7
(q7,L)=q8
(q8,D)=q7