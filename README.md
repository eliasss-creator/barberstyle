<div align="center">

```text
  ____             _               ____  _         _       
 | __ )  __ _ _ __| |__   ___ _ __/ ___|| |_ _   _| | ___  
 |  _ \ / _` | '__| '_ \ / _ \ '__\___ \| __| | | | |/ _ \ 
 | |_) | (_| | |  | |_) |  __/ |   ___) | |_| |_| | |  __/ 
 |____/ \__,_|_|  |_.__/ \___|_|  |____/ \__|\__, |_|\___| 
                                             |___/         
```

### Sistema de Gestão Completa para Barbearias

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-2ea44f?style=for-the-badge)](#)

[Sobre](#-sobre-o-projeto) •
[Preview](#-preview-do-sistema) •
[Funcionalidades](#-funcionalidades) •
[Tecnologias](#-tecnologias-utilizadas) •
[Como Executar](#-como-executar-o-projeto)

</div>

---

## 💻 Sobre o Projeto

O **BarberStyle** é uma aplicação desktop desenvolvida em Java para modernizar e organizar a rotina administrativa e operacional de barbearias. O sistema centraliza o controle de agendamentos, cadastro de clientes, gestão de barbeiros, catálogo de serviços e acompanhamento financeiro em uma interface intuitiva.
Clientes não são obrigados a realizar cadastros para se agendarem caso se interessem por algum serviço oferecido por um barbeiro em determinada Barbearia.
---

## 📸 Preview do Sistema

<div align="center">
  <img src="assets/preview.png" alt="Tela Principal do BarberStyle" width="800px" />
</div>

---

## ⚙️ Funcionalidades

- [x] **Autenticação de Usuários:** Controle de acesso seguro para administradores e barbeiros.
- [x] **Gestão de Agendamentos:** Marcação, edição, cancelamento e consulta de horários por profissional.
- [x] **Cadastro de Clientes e Barbeiros:** Histórico completo de atendimentos e dados de contato.
- [x] **Catálogo de Serviços e Produtos:** Controle de preços, duração média e estoque básico.
- [ ] **Relatórios Financeiros em PDF:** Exportação de fechamento diário e mensal *(em breve)*.

---

## 🛠️ Tecnologias Utilizadas

| Camada / Ferramenta | Tecnologia | Finalidade |
| :--- | :--- | :--- |
| **Linguagem** | Java (JDK 17+) | Lógica de negócio e regras da aplicação |
| **Interface Gráfica** | Java Swing / FlatLaf | Telas desktop responsivas e modernas |
| **Gerenciador de Build** | Apache Maven | Controle de dependências e empacotamento (`.jar`) |
| **Banco de Dados** | MySQL | Persistência relacional de dados |
| **Segurança** | Arquivo `.properties` | Isolamento de credenciais sensíveis fora do repositório |

---

## 📂 Estrutura do Projeto

```text
BarberStyle/
├── database/
│   └── dump_teste.sql             # Script SQL para criação das tabelas e dados iniciais
├── docs/
│   └── preview.png                # Captura de tela da aplicação
├── src/
│   └── main/java/br/com/barbearia/barberstyle/
│       └── BarberStyle.java       # Classe principal de inicialização
├── .gitignore                     # Arquivos ignorados pelo Git (credenciais e build)
├── config.properties.example      # Modelo de configuração do banco de dados
└── pom.xml                        # Dependências e configurações do Maven
```

---

## 🚀 Como Executar o Projeto

### 1. Pré-requisitos
Certifique-se de ter instalado em sua máquina:
- **Java JDK 17** ou superior
- **Apache Maven**
- **MySQL Server** + Mysql Workbench(recomendado para testar localmente) ou **MySQL Server** (ou XAMPP / Laragon)

### 2. Clonar o repositório
```bash
[] git clone (https://github.com/eliasss-creator/barberstyle.git)  
cd barberstyle
```

### 3. Configurar o Banco de Dados
1. Crie um banco de dados no MySQL (ex: `barberstyle_db`).
2. Importe o script localizado em `database/dump_teste.sql` para gerar a estrutura das tabelas.

### 4. Configurar as Credenciais
1. Na raiz do projeto, faça uma cópia do arquivo `config.properties.example` e renomeie para `config.properties`.
2. Preencha com os dados locais do seu MySQL:
```properties
db.url=jdbc:mysql://localhost:3306/barberstyle_db
db.user=seu_usuario_mysql
db.password=sua_senha_mysql
```

### 5. Compilar e Rodar
```bash
mvn clean compile exec:java -Dexec.mainClass="br.com.barbearia.barberstyle.BarberStyle"
```

---

<div align="center">
  Desenvolvido para estudo e portfólio de engenharia de software.
</div>

---

## 📄 Licença e Direitos Autorais

Copyright © 2026 **Elias** (BarberStyle). Todos os direitos reservados.

Este código-fonte é disponibilizado exclusivamente para fins de demonstração e portfólio. Nenhuma parte deste projeto pode ser reproduzida, distribuída ou utilizada comercialmente sem autorização prévia por escrito do autor.

---

<div align="center">
  Desenvolvido por <strong>Elias</strong> • © 2026 BarberStyle — Todos os direitos reservados.
</div>