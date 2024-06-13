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
- Criar o created_at para salvar o timestamp do processo

- Se que poderia trabalhar com uma tabela, para melhor interagir com os tipos, porém preferi adotar uma pratica de colocar como varchar pois posso usar isso na validação do banco de dados
- Adicionar o balance BigDecimal
- Adicionar o version

```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_type VARCHAR(10) NOT NULL CHECK (user_type IN ('COMUM', 'LOJISTA')),
    full_name VARCHAR(255) NOT NULL,
    tax_number VARCHAR(18) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    balance NUMERIC(19, 2) DEFAULT 0.0,
    version BIGINT DEFAULT 0,

    CONSTRAINT tax_number_valid CHECK (validate_tax_number(user_type, tax_number))
);
```

- Criação de funções:

```sql
-- Função para validar CPF
CREATE OR REPLACE FUNCTION validate_cpf(user_type VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN user_type = 'COMUM';
END;
$$ LANGUAGE plpgsql;

-- Função para validar CNPJ
CREATE OR REPLACE FUNCTION validate_cnpj(user_type VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN user_type = 'LOJISTA';
END;
$$ LANGUAGE plpgsql;

-- Função para validar TaxNumber
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
INSERT INTO users (user_type, full_name, tax_number, email, password_hash, balance)
VALUES
(
    'COMUM',
    'Usuario Comum',
    '12345678901',
    'usuario.comum@example.com',
    'hashed_password_123',
    1000.00
),(
    'LOJISTA',
    'Usuario Lojista',
    '12345678000195',
    'usuario.lojista@example.com',
    'hashed_password_456',
    5000.00
);
```