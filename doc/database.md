## Definições de banco de dados:
### Requerimentos
- Para ambos **tipos de usuário**, precisamos do **Nome Completo**, **CPF**, **e-mail** e **Senha**
- **CPF/CNPJ e e-mails** devem ser **únicos no sistema**
- Sendo assim, seu sistema deve permitir apenas um cadastro com o mesmo CPF ou endereço de e-mail;
---

```
Brazil:
CPF: 11 digits (XXX.XXX.XXX-XX)
CNPJ: 14 digits (XX.XXX.XXX/XXXX-XX)

Obs: separação da pontuação, pois isso e formatação
```
### Tabela: USER

**Desição Tecnica:**
- Criar o **created_at** para salvar o timestamp do processo

- **Se que poderia trabalhar com uma tabela, para melhor interagir com os tipos, porém preferi adotar uma pratica de colocar como varchar pois posso usar isso na validação do banco de dados**
- Adicionar o **balance**
- Adicionar o **version**
- Acredito que a senha deva ser um **Hash + Salt**

```sql
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    user_type VARCHAR(10) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,

    -- CPF ou CNPJ
    tax_number VARCHAR(18) UNIQUE NOT NULL,

    -- Informacoes de senha
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(255) NOT NULL,

    -- Saldo
    balance NUMERIC(19, 2) DEFAULT 0.0,

    -- Informacoes de criacao e atualizacao
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Configuracao para usar a funcao de validacao de CPF e CNPJ
    CONSTRAINT tax_number_valid CHECK (validate_tax_number(user_type, tax_number))
);
```

- Criação de funções:

```sql
-- Funcao para validar CPF
CREATE OR REPLACE FUNCTION validate_cpf(user_type VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    -- TODO: Colocar validação de CPF em plpgsql
    RETURN user_type = 'COMUM';
END;
$$ LANGUAGE plpgsql;

-- Funcao para validar CNPJ
CREATE OR REPLACE FUNCTION validate_cnpj(user_type VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    -- TODO: Colocar validação de CNPJ em plpgsql
    RETURN user_type = 'LOJISTA';
END;
$$ LANGUAGE plpgsql;

-- Funcao para "validar" TaxNumber
CREATE OR REPLACE FUNCTION validate_tax_number(user_type VARCHAR, tax_number VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    IF LENGTH(tax_number) = 14 THEN
        RETURN validate_cpf(user_type);
    ELSIF LENGTH(tax_number) = 18 THEN
        RETURN validate_cnpj(user_type);
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;
```
- Inserções
```sql
INSERT INTO users (
    user_type,
    full_name,
    tax_number,
    email,
    password_salt,
    password_hash,
    balance
) VALUES(
    'COMUM',
    'Usuario Comum',
    '12345678901',
    'usuario.comum@example.com',
    'd057fc7347562abae79574604adfb2d729b1b59055c6b496a8079c7fc2ca59d7',
    '9753f72a13ce82702e16b55825a17f7cf92a2941bfa7f8c35871a423e35bb116',
    1000.00
),(
    'LOJISTA',
    'Usuario Lojista',
    '12345678000195',
    'usuario.lojista@example.com',
    '1a00e1a179373aa9b13d3e6765b809ec91a4d0cddf406214a144516466125011',
    '9b1f560dec75c06882073667ed0d760691fc4b03a0ad81415ec7315dbef82dc2',
    5000.00
);
```

Exemplo do Hash com Salt:
```
# COMUM
Senha: MinhaSenha
Password Salt: d057fc7347562abae79574604adfb2d729b1b59055c6b496a8079c7fc2ca59d7
Password Hash: 9753f72a13ce82702e16b55825a17f7cf92a2941bfa7f8c35871a423e35bb116

# LOJISTA
Senha: MinhaSenha
Password Salt: 1a00e1a179373aa9b13d3e6765b809ec91a4d0cddf406214a144516466125011
Password Hash: 9b1f560dec75c06882073667ed0d760691fc4b03a0ad81415ec7315dbef82dc2
```

### Tabela: TRANSACTION

```sql
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    payer BIGINT NOT NULL,
    payee BIGINT NOT NULL,
    value NUMERIC(19, 2) NOT NULL,

    -- Regra para report para algumas estrategias futuras
    notified BOOLEAN DEFAULT FALSE,

    -- Informacoes de criacao e atualizacao
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Vinculacao das tabelas
    CONSTRAINT fk_payer FOREIGN KEY (payer) REFERENCES users(id),
    CONSTRAINT fk_payee FOREIGN KEY (payee) REFERENCES users(id),
);
```