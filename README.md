# Simplify Transfer in Spring

## Regras de negócio:
A seguir estão algumas regras de negócio que são importantes para o funcionamento do micro serviço de transferencia simplificada:

- Para ambos tipos de usuário, precisamos do Nome Completo, CPF, e-mail e Senha. CPF/CNPJ e e-mails devem ser únicos no sistema. Sendo assim, seu sistema deve permitir apenas um cadastro com o mesmo CPF ou endereço de e-mail;

- Usuários podem enviar dinheiro (efetuar transferência) para lojistas e entre usuários;
    ```mermaid
    sequenceDiagram
        box rgb(34, 139, 34)
            participant USER as Usuário Comum
            participant LJ as Lojista
        end

        Note over USER,LJ: Usuario para Lojista
        USER-->>LJ: 
        Note over USER,USER: Usuario para Usuario
        USER-->>USER: |
    ```

- Lojistas só recebem transferências, não enviam dinheiro para ninguém;
    ```mermaid
    sequenceDiagram
        box rgb(199, 0, 57 )
            participant USER as Usuário Comum
            participant LJ as Lojista
        end

        Note over USER,LJ: Lojista para Usuario
        LJ-->>USER: 
        Note over LJ,LJ: Lojista para Lojista
        LJ-->>LJ: |
    ```

- Validar se o usuário tem saldo antes da transferência;

- Antes de finalizar a transferência, deve-se consultar um serviço autorizador externo, use este mock https://util.devi.tools/api/v2/authorize para simular o serviço utilizando o verbo GET;

    ```
    Processo bloqueante, deve ser sincrono!
    ```
    ```
    GET https://util.devi.tools/api/v2/authorize

    Response OK: 200 OK
    {
        "status": "success",
        "data": {
            "authorization": true
        }
    }

    Response Error: 403 Forbidden
    {
        "status": "fail",
        "data": {
            "authorization": false
        }
    }
    ```


- A operação de transferência deve ser uma transação (ou seja, revertida em qualquer caso de inconsistência) e o dinheiro deve voltar para a carteira do usuário que envia;

    ```
    Transacional e exceptions, pois tem o Rollback do banco
    ```

- No recebimento de pagamento, o usuário ou lojista precisa receber notificação (envio de email, sms) enviada por um serviço de terceiro e eventualmente este serviço pode estar indisponível/instável. Use este mock https://util.devi.tools/api/v1/notify para simular o envio da notificação utilizando o verbo POST;

    ```
    POST https://util.devi.tools/api/v1/notify
    Content-Type: application/json

    Response OK:
    statusCode: 204 No Content

    Response Error:
    statusCode: 504 Gateway Timeout
    {
        "status": "error",
        "message": "The service is not available, try again later"
    }
    ```

- Este serviço deve ser RESTFul.

## Endpoint de transferência
Você pode implementar o que achar conveniente, porém vamos nos atentar somente ao fluxo de transferência entre dois usuários. A implementação deve seguir o contrato abaixo.

```
POST /transfer
Content-Type: application/json

Body:
{
  "value": 100.0,
  "payer": 4,
  "payee": 15
}
```