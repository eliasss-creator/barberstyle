```text
 ____             _               ____  _         _       
| __ )  __ _ _ __| |__   ___ _ __/ ___|| |_ _   _| | ___  
|  _ \ / _` | '__| '_ \ / _ \ '__\___ \| __| | | | |/ _ \ 
| |_) | (_| | |  | |_) |  __/ |   ___) | |_| |_| | |  __/ 
|____/ \__,_|_|  |_.__/ \___|_|  |____/ \__|\__, |_|\___| 
                                            |___/         
        Sistema de Gestão e Agendamento para Barbearias
```

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans%20IDE-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white)

## Sobre o Projeto

O **BarberStyle** é uma aplicação desktop desenvolvida em Java para automatizar a rotina operacional de barbearias. O sistema conta com controle de acesso baseado em perfis (**Administrador**, **Secretaria**, **Barbeiro** e **Cliente**), gerenciamento de equipe, agendamentos e armazenamento seguro de credenciais com criptografia **SHA-256**.

---

## Arquitetura do Projeto

```text
BarberStyle/
├── database/
│   └── dump_teste.sql               # Estrutura das tabelas e dados fictícios para teste
├── src/
│   └── main/
│       └── java/
│           └── br/com/barbearia/barberstyle/
│               ├── config/          # Gerenciamento de conexão JDBC (Conexao.java)
│               ├── dao/             # Camada de acesso a dados (UsuarioDAO, etc.)
│               ├── model/           # Entidades e regras de negócio (Admin, Barbeiro, Cliente)
│               ├── util/            # Utilitários de segurança e sessão (SegurancaUtil, Sessao)
│               └── view/            # Interfaces gráficas (Swing)
├── config.properties.example        # Modelo de configuração de acesso ao banco
├── pom.xml                          # Dependências e build do Apache Maven
└── README.md                        # Documentação do projeto
```

---

## Pré-requisitos

Antes de iniciar, certifique-se de ter instalado em sua máquina:
- **Java Development Kit (JDK)** versão 17 ou superior
- **MySQL Server** e **MySQL Workbench** (versão 8.0+)
- **Apache Maven** (ou **NetBeans IDE**, que já possui suporte nativo)
- **Git**

---

## Instalação e Configuração

### 1. Clonar o repositório
```bash
git clone [https://github.com/SEU-USUARIO/SEU-REPOSITORIO.git](https://github.com/SEU-USUARIO/SEU-REPOSITORIO.git)
cd SEU-REPOSITORIO
```

### 2. Importar o Banco de Dados (MySQL Workbench)
1. Abra o **MySQL Workbench** e conecte-se ao seu servidor local.
2. No menu superior, acesse **Server** > **Data Import**.
3. Selecione a opção **Import from Self-Contained File** e escolha o arquivo `database/dump_teste.sql` localizado na pasta do projeto.
4. Em **Default Target Schema**, clique em **New...**, digite `barber_style` e confirme.
5. Clique na aba **Import Progress** (canto inferior direito) e clique em **Start Import**.

### 3. Configurar as Credenciais do Banco
Por segurança, o arquivo real de conexão não é versionado no repositório.
1. Na raiz do projeto, faça uma cópia do arquivo `config.properties.example`.
2. Renomeie a cópia para `config.properties`.
3. Abra o `config.properties` e preencha com os dados do seu MySQL local:
   ```properties
   db.url=jdbc:mysql://localhost:3306/barber_style
   db.user=root
   db.password=sua_senha_do_mysql
   ```

### 4. Executar o Projeto
1. Abra o **NetBeans IDE**.
2. Vá em **Arquivo** > **Abrir Projeto** e selecione a pasta `BarberStyle`.
3. Aguarde o Maven baixar as dependências do `pom.xml`.
4. Pressione **`F6`** (ou clique em **Executar Projeto**) para iniciar a aplicação.

---

## Credenciais de Acesso para Teste

Caso tenha importado o arquivo `database/dump_teste.sql`, utilize as contas abaixo para testar os diferentes níveis de permissão do sistema:

| Perfil | E-mail de Acesso | Senha |
| :--- | :--- | :--- |
| **Administrador** | `admin@barberstyle.com` | `Admin@123` |
| **Secretaria** | `secretaria@barberstyle.com` | `Sec@123` |
| **Barbeiro** | `barbeiro@barberstyle.com` | `Barb@123` |

*(Ajuste a tabela acima com um usuário real que exista dentro do seu `dump_teste.sql`).*

---

## Segurança Implementada
- **Isolamento de Credenciais:** Uso de `config.properties` ignorado via `.gitignore`.
- **Proteção contra SQL Injection:** Todas as consultas utilizam `PreparedStatement` parametrizado.
- **Criptografia de Senhas:** Senhas armazenadas no banco exclusivamente em hash **SHA-256** (`SegurancaUtil`).
- **Transações Atômicas:** Controle manual de `commit` e `rollback` em cadastros compostos.