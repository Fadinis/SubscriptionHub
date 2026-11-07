import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def generate_architecture_diagram(output_path):
    fig, ax = plt.subplots(figsize=(12, 7.5), dpi=300)
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 7.5)
    ax.axis('off')

    # Background color
    fig.patch.set_facecolor('#F8F9FA')
    ax.set_facecolor('#F8F9FA')

    # Title
    ax.text(6, 7.1, "Subscription Hub — Arquitetura de Microsserviços (Sprint 07)",
            ha='center', va='center', fontsize=15, fontweight='bold', color='#1A365D')

    # 1. Clients Layer
    client_box = patches.FancyBboxPatch((0.5, 5.4), 11, 1.3, boxstyle="round,pad=0.1",
                                        edgecolor="#CBD5E0", facecolor="#EDF2F7", lw=1.5, ls="--")
    ax.add_patch(client_box)
    ax.text(0.8, 6.4, "CAMADA DE APRESENTAÇÃO (CLIENTES)", fontsize=9, fontweight='bold', color='#4A5568')

    # Client apps
    web_box = patches.FancyBboxPatch((1.5, 5.6), 4, 0.7, boxstyle="round,pad=0.08",
                                     edgecolor="#ED8936", facecolor="#FEEBC8", lw=1.5)
    ax.add_patch(web_box)
    ax.text(3.5, 5.95, "Frontend Web / Mobile (SPA / App)", ha='center', va='center', fontsize=10, fontweight='bold', color='#7B341E')

    swing_box = patches.FancyBboxPatch((6.5, 5.6), 4, 0.7, boxstyle="round,pad=0.08",
                                       edgecolor="#ED8936", facecolor="#FEEBC8", lw=1.5)
    ax.add_patch(swing_box)
    ax.text(8.5, 5.95, "Aplicacao Desktop (Java Swing Original)", ha='center', va='center', fontsize=10, fontweight='bold', color='#7B341E')

    # API Gateway
    gw_box = patches.FancyBboxPatch((3.5, 4.3), 5, 0.75, boxstyle="round,pad=0.08",
                                    edgecolor="#4299E1", facecolor="#EBF8FF", lw=1.5, ls=":")
    ax.add_patch(gw_box)
    ax.text(6, 4.67, "API Gateway (Porta 8080) [Sprint Futura]\nRoteamento Reverso & Seguranca Unificada",
            ha='center', va='center', fontsize=9, fontweight='bold', color='#2B6CB0')

    # Arrows from Clients to Gateway
    ax.annotate('', xy=(4.8, 5.05), xytext=(3.5, 5.55),
                arrowprops=dict(arrowstyle="->", color="#A0AEC0", lw=1.2, ls="--"))
    ax.annotate('', xy=(7.2, 5.05), xytext=(8.5, 5.55),
                arrowprops=dict(arrowstyle="->", color="#A0AEC0", lw=1.2, ls="--"))

    # Microservices Container
    ms_container = patches.FancyBboxPatch((0.5, 0.4), 11, 3.6, boxstyle="round,pad=0.1",
                                          edgecolor="#CBD5E0", facecolor="#FFFFFF", lw=1.5)
    ax.add_patch(ms_container)
    ax.text(0.8, 3.75, "DOMINIO DE MICROSSERVICOS (ISOLAMENTO DE DADOS & REGRAS DE NEGOCIO)", fontsize=9, fontweight='bold', color='#2D3748')

    # MS1: User Service
    ms1_box = patches.FancyBboxPatch((0.8, 1.4), 2.4, 2.0, boxstyle="round,pad=0.08",
                                     edgecolor="#3182CE", facecolor="#EBF8FF", lw=2)
    ax.add_patch(ms1_box)
    ax.text(2.0, 3.0, "User Service\n(MS-1)", ha='center', va='center', fontsize=10, fontweight='bold', color='#2B6CB0')
    ax.text(2.0, 2.5, "Porta: 8081\nStatus: IMPLEMENTADO", ha='center', va='center', fontsize=8, color='#2C5282')
    db1_box = patches.FancyBboxPatch((1.0, 1.6), 2.0, 0.5, boxstyle="round,pad=0.05",
                                     edgecolor="#4A5568", facecolor="#EDF2F7", lw=1)
    ax.add_patch(db1_box)
    ax.text(2.0, 1.85, "DB: users.json", ha='center', va='center', fontsize=8, fontweight='bold', color='#4A5568')

    # MS2: Subscription Service
    ms2_box = patches.FancyBboxPatch((3.6, 1.4), 2.5, 2.0, boxstyle="round,pad=0.08",
                                     edgecolor="#3182CE", facecolor="#EBF8FF", lw=2)
    ax.add_patch(ms2_box)
    ax.text(4.85, 3.0, "Subscription Service\n(MS-2)", ha='center', va='center', fontsize=10, fontweight='bold', color='#2B6CB0')
    ax.text(4.85, 2.5, "Porta: 8082\nStatus: IMPLEMENTADO", ha='center', va='center', fontsize=8, color='#2C5282')
    db2_box = patches.FancyBboxPatch((3.85, 1.6), 2.0, 0.5, boxstyle="round,pad=0.05",
                                     edgecolor="#4A5568", facecolor="#EDF2F7", lw=1)
    ax.add_patch(db2_box)
    ax.text(4.85, 1.85, "DB: subscriptions.json", ha='center', va='center', fontsize=8, fontweight='bold', color='#4A5568')

    # MS3: Notification Service
    ms3_box = patches.FancyBboxPatch((6.5, 1.4), 2.4, 2.0, boxstyle="round,pad=0.08",
                                     edgecolor="#3182CE", facecolor="#EBF8FF", lw=2)
    ax.add_patch(ms3_box)
    ax.text(7.7, 3.0, "Notification Service\n(MS-3)", ha='center', va='center', fontsize=10, fontweight='bold', color='#2B6CB0')
    ax.text(7.7, 2.5, "Porta: 8083\nStatus: IMPLEMENTADO", ha='center', va='center', fontsize=8, color='#2C5282')
    db3_box = patches.FancyBboxPatch((6.7, 1.6), 2.0, 0.5, boxstyle="round,pad=0.05",
                                     edgecolor="#4A5568", facecolor="#EDF2F7", lw=1)
    ax.add_patch(db3_box)
    ax.text(7.7, 1.85, "DB: notifications.json", ha='center', va='center', fontsize=8, fontweight='bold', color='#4A5568')

    # MS4: Reporting Service
    ms4_box = patches.FancyBboxPatch((9.2, 1.4), 2.0, 2.0, boxstyle="round,pad=0.08",
                                     edgecolor="#A0AEC0", facecolor="#F7FAFC", lw=1.5, ls="--")
    ax.add_patch(ms4_box)
    ax.text(10.2, 3.0, "Reporting Service\n(MS-4)", ha='center', va='center', fontsize=9, fontweight='bold', color='#718096')
    ax.text(10.2, 2.5, "Porta: 8084\n(Sprint Futura)", ha='center', va='center', fontsize=8, color='#A0AEC0')
    db4_box = patches.FancyBboxPatch((9.3, 1.6), 1.8, 0.5, boxstyle="round,pad=0.05",
                                     edgecolor="#CBD5E0", facecolor="#EDF2F7", lw=1)
    ax.add_patch(db4_box)
    ax.text(10.2, 1.85, "DB: reports_cache", ha='center', va='center', fontsize=8, color='#A0AEC0')

    # Arrows for REST Communications
    # Communication 1: Subscription -> User
    ax.annotate('', xy=(3.2, 2.3), xytext=(3.6, 2.3),
                arrowprops=dict(arrowstyle="->", color="#E53E3E", lw=2.5))
    ax.text(3.4, 0.95, "(1) REST GET /api/users/{id}\nValida existência do usuário",
            ha='center', va='center', fontsize=7.5, fontweight='bold', color='#C53030',
            bbox=dict(boxstyle="square,pad=0.3", facecolor="#FFF5F5", edgecolor="#FEB2B2", lw=1))

    # Communication 2: Notification -> Subscription
    ax.annotate('', xy=(6.1, 2.3), xytext=(6.5, 2.3),
                arrowprops=dict(arrowstyle="->", color="#38A169", lw=2.5))
    ax.text(6.3, 0.95, "(2) REST GET /api/subscriptions/user/{id}\nConsulta assinaturas a vencer",
            ha='center', va='center', fontsize=7.5, fontweight='bold', color='#276749',
            bbox=dict(boxstyle="square,pad=0.3", facecolor="#F0FFF4", edgecolor="#9AE6B4", lw=1))

    # Legend
    leg1 = patches.Rectangle((0.8, 0.55), 0.3, 0.2, facecolor="#EBF8FF", edgecolor="#3182CE", lw=1.5)
    ax.add_patch(leg1)
    ax.text(1.2, 0.65, "Microsserviço Implementado na Sprint 07", va='center', fontsize=8, color='#2D3748')

    leg2 = patches.Rectangle((4.8, 0.55), 0.3, 0.2, facecolor="#F7FAFC", edgecolor="#A0AEC0", lw=1.5, ls="--")
    ax.add_patch(leg2)
    ax.text(5.2, 0.65, "Componente Projetado (Sprint Futura)", va='center', fontsize=8, color='#718096')

    leg3 = patches.Rectangle((8.6, 0.55), 0.3, 0.2, facecolor="#FEEBC8", edgecolor="#ED8936", lw=1.5)
    ax.add_patch(leg3)
    ax.text(9.0, 0.65, "Camada Cliente / Apresentação", va='center', fontsize=8, color='#7B341E')

    plt.tight_layout()
    plt.savefig(output_path, dpi=300, facecolor=fig.get_facecolor(), edgecolor='none')
    plt.close()
    print(f"Diagrama gerado com sucesso em: {output_path}")

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def create_docx(diagram_image_path, docx_output_path):
    doc = Document()

    # Set page margins (2 cm)
    for section in doc.sections:
        section.top_margin = Inches(0.8)
        section.bottom_margin = Inches(0.8)
        section.left_margin = Inches(0.8)
        section.right_margin = Inches(0.8)

    # Document Header / Title
    title_p = doc.add_paragraph()
    title_run = title_p.add_run("Documento de Arquitetura de Microsserviços")
    title_run.font.name = "Calibri"
    title_run.font.size = Pt(24)
    title_run.font.bold = True
    title_run.font.color.rgb = RGBColor(26, 54, 93) # Navy blue
    title_p.paragraph_format.space_after = Pt(4)

    sub_p = doc.add_paragraph()
    sub_run = sub_p.add_run("Projeto: Subscription Hub")
    sub_run.font.name = "Calibri"
    sub_run.font.size = Pt(14)
    sub_run.font.bold = True
    sub_run.font.color.rgb = RGBColor(74, 85, 104)

    meta_p = doc.add_paragraph()
    meta_p.paragraph_format.space_after = Pt(16)
    m1 = meta_p.add_run("Disciplina: ")
    m1.font.bold = True
    meta_p.add_run("Práticas de Arquitetura e Engenharia de Software (PAES)\n")
    m2 = meta_p.add_run("Sprint 07: ")
    m2.font.bold = True
    meta_p.add_run("Arquitetura, Implementação e Testes de Microsserviços\n")
    m3 = meta_p.add_run("Responsável Arquitetura: ")
    m3.font.bold = True
    meta_p.add_run("Eduardo Fadini (Integrante 1 — User Service)")

    doc.add_heading("1. Visão Geral da Arquitetura", level=1)
    p1 = doc.add_paragraph(
        "O Subscription Hub evoluiu de uma arquitetura monolítica centrada em interface gráfica desktop "
        "(Java Swing com persistência em arquivos CSV) para uma Arquitetura Baseada em Microsserviços."
    )
    p1.paragraph_format.space_after = Pt(6)

    p2 = doc.add_paragraph("A solução foi projetada para desacoplar as áreas de negócio do sistema, proporcionando:")
    p2.paragraph_format.space_after = Pt(4)

    b1 = doc.add_paragraph(style='List Bullet')
    r = b1.add_run("Isolamento de falhas: "); r.font.bold = True
    b1.add_run("a indisponibilidade de um serviço (como notificações) não bloqueia os demais.")

    b2 = doc.add_paragraph(style='List Bullet')
    r = b2.add_run("Escalabilidade independente: "); r.font.bold = True
    b2.add_run("serviços com maior volume de requisições podem ser redimensionados isoladamente.")

    b3 = doc.add_paragraph(style='List Bullet')
    r = b3.add_run("Independência de deploy e desenvolvimento: "); r.font.bold = True
    b3.add_run("cada módulo é mantido e versionado por desenvolvedores diferentes sem conflitos de merge.")

    doc.add_heading("2. Diagrama da Arquitetura Completa", level=1)
    dp = doc.add_paragraph(
        "O diagrama a seguir ilustra a arquitetura completa da solução, destacando visualmente a camada de apresentação, "
        "o API Gateway centralizador e os microsserviços do domínio, com ênfase nos 3 serviços implementados na Sprint 07 e "
        "as comunicações REST síncronas estabelecidas entre eles:"
    )
    dp.paragraph_format.space_after = Pt(8)

    if os.path.exists(diagram_image_path):
        doc.add_picture(diagram_image_path, width=Inches(6.8))
        caption_p = doc.add_paragraph()
        caption_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        c_run = caption_p.add_run("Figura 1 — Diagrama Arquitetural de Microsserviços do Subscription Hub")
        c_run.font.italic = True
        c_run.font.size = Pt(9)
        c_run.font.color.rgb = RGBColor(113, 128, 150)
        caption_p.paragraph_format.space_after = Pt(14)

    doc.add_heading("3. Especificação dos Microsserviços", level=1)
    sp = doc.add_paragraph(
        "A tabela abaixo resume a decomposição funcional do sistema, estabelecendo o escopo de negócio, "
        "a titularidade exclusiva dos dados e as dependências inter-serviços:"
    )
    sp.paragraph_format.space_after = Pt(8)

    # Table creation
    table_data = [
        ["Microsserviço", "Responsabilidade Principal", "Funcionalidades Oferecidas", "Dados Próprios", "Comunicação & Dependências", "Status Sprint 07"],
        [
            "User Service\n(MS-1)",
            "Gestão de identidade, autenticação e dados de perfil do usuário",
            "• Cadastro de usuário\n• Autenticação (Login)\n• Consulta de perfil por ID",
            "data/users.json\n(id, nome, email, senha)",
            "Provedor REST síncrono para o Subscription Service na porta 8081.",
            "IMPLEMENTADO\n(Eduardo Fadini)"
        ],
        [
            "Subscription Service\n(MS-2)",
            "Ciclo de vida e gestão de assinaturas recorrentes",
            "• Criação de assinatura\n• Consulta por usuário/ID\n• Cancelamento de plano",
            "data/subscriptions.json\n(id, usuarioId, valor, vencimento, status)",
            "Consome via REST o User Service (GET /api/users/{id}) para validar existência antes do cadastro.",
            "IMPLEMENTADO\n(Fabrício Filho)"
        ],
        [
            "Notification Service\n(MS-3)",
            "Cálculo de vencimentos e disparo de alertas de cobrança",
            "• Verificação diária\n• Antecedência configurável\n• Marcação de lida",
            "data/notifications.json\n(id, usuarioId, assinaturaId, mensagem, lida)",
            "Consome via REST o Subscription Service (GET /api/subscriptions/user/{id}) para obter assinaturas ativas.",
            "IMPLEMENTADO\n(Yago Santos)"
        ],
        [
            "Reporting Service\n(MS-4)",
            "Consolidação analítica e geração de métricas financeiras",
            "• Gastos por categoria\n• Totais mensais e anuais\n• Exportação de relatórios",
            "reports_cache\n(Dados consolidados de métricas)",
            "Consome o Subscription Service para coletar dados históricos consolidados.",
            "PROJETADO\n(Sprint Futura)"
        ]
    ]

    table = doc.add_table(rows=len(table_data), cols=6)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False

    col_widths = [Inches(1.1), Inches(1.3), Inches(1.3), Inches(1.1), Inches(1.4), Inches(1.0)]

    for row_idx, row in enumerate(table.rows):
        is_header = (row_idx == 0)
        trPr = row._tr.get_or_add_trPr()
        trPr.append(parse_xml(f'<w:cantSplit {nsdecls("w")}/>'))

        for col_idx, cell in enumerate(row.cells):
            cell.width = col_widths[col_idx]
            cell.text = table_data[row_idx][col_idx]
            set_cell_margins(cell, top=80, bottom=80, left=100, right=100)

            # Cell formatting
            for paragraph in cell.paragraphs:
                paragraph.paragraph_format.space_before = Pt(0)
                paragraph.paragraph_format.space_after = Pt(0)
                for run in paragraph.runs:
                    run.font.name = "Calibri"
                    if is_header:
                        run.font.bold = True
                        run.font.size = Pt(9.5)
                        run.font.color.rgb = RGBColor(255, 255, 255)
                    else:
                        run.font.size = Pt(8.5)

            if is_header:
                set_cell_background(cell, "1A365D")
            else:
                if row_idx % 2 == 1:
                    set_cell_background(cell, "F7FAFC")
                else:
                    set_cell_background(cell, "EDF2F7")

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    doc.add_heading("4. Justificativa Arquitetural", level=1)

    doc.add_heading("4.1. Critérios de Decomposição (Bounded Contexts & SRP)", level=2)
    p_just = doc.add_paragraph(
        "A divisão do monolito em microsserviços foi orientada pelos conceitos de Bounded Contexts (Contextos Delimitados) "
        "do Domain-Driven Design (DDD) e pelo Princípio da Responsabilidade Única (SRP):\n\n"
        "1. Domínio de Identidade e Acesso (User Service): Possui ciclo de vida autônomo, requisitos específicos de validação "
        "de credenciais e unicidade de cadastro, com frequência de alteração e leitura distinta dos planos financeiros.\n"
        "2. Domínio de Faturamento e Assinaturas (Subscription Service): Constitui o núcleo transacional da plataforma, "
        "sofrendo operações frequentes de alteração de valores, status e datas de vencimento.\n"
        "3. Domínio de Alertas e Notificações (Notification Service): Apresenta natureza temporal e assíncrona (processamento batch "
        "ou verificação de prazos por data), não devendo concorrer com o banco de dados transacional de assinaturas."
    )
    p_just.paragraph_format.space_after = Pt(8)

    doc.add_heading("4.2. Responsabilidade sobre os Dados (Database-per-Service)", level=2)
    p_data = doc.add_paragraph(
        "Cada microsserviço possui controle exclusivo e isolado sobre seu armazenamento em disco:\n"
        "• User Service administra exclusivamente o arquivo data/users.json.\n"
        "• Subscription Service administra exclusivamente data/subscriptions.json.\n"
        "• Notification Service administra exclusivamente data/notifications.json.\n\n"
        "Não é permitido sob nenhuma hipótese o acesso direto de um serviço à base de outro. Qualquer necessidade "
        "de informação cruzada ocorre exclusivamente por chamadas HTTP REST com payload JSON."
    )
    p_data.paragraph_format.space_after = Pt(8)

    doc.add_heading("4.3. Comunicação Inter-serviços e Resiliência", level=2)
    p_comm = doc.add_paragraph(
        "A comunicação entre serviços é síncrona via HTTP REST utilizando JSON como formato de intercâmbio:\n\n"
        "• Operação 1 (Obrigatória — Seção 3.2): Durante o cadastro de uma nova assinatura (POST /api/subscriptions), o Subscription "
        "Service executa um GET HTTP síncrono no User Service (http://localhost:8081/api/users/{id}). Se o usuário for encontrado (200 OK), "
        "a assinatura é criada (201 Created). Se o usuário não existir (404 Not Found), a operação é rejeitada com 422 Unprocessable Entity.\n\n"
        "• Tolerância a Falhas e Circuit Breaker Conceitual: O cliente HTTP (UserClient) possui timeout estrito de 3 segundos. "
        "Caso o User Service esteja inacessível ou fora do ar, o Subscription Service captura a falha e retorna 503 Service Unavailable, "
        "impedindo a gravação de dados inconsistentes ou órfãos no sistema."
    )
    p_comm.paragraph_format.space_after = Pt(8)

    doc.add_heading("4.4. Benefícios e Dificuldades da Arquitetura", level=2)
    p_eval = doc.add_paragraph(
        "• Benefícios Alcançados:\n"
        "  - Autonomia de Equipe: Os 3 integrantes do grupo puderam desenvolver, testar e versionar seus microsserviços em branches separadas "
        "sem gerar conflitos de código.\n"
        "  - Isolamento de Falhas: A queda do serviço de notificações não impede o login ou o cadastro de assinaturas.\n"
        "  - Testabilidade Elevada: A arquitetura desacoplada viabilizou testes unitários isolados com mocks rápidos via Mockito.\n\n"
        "• Dificuldades e Mitigações:\n"
        "  - Orquestração de Múltiplos Processos: Mitigada através de scripts automatizados de compilação (build-all.bat) e execução (start-all.bat).\n"
        "  - Latência de Rede: Minimizada pelo uso de contratos REST compactos e DTOs simplificados."
    )
    p_eval.paragraph_format.space_after = Pt(8)

    doc.save(docx_output_path)
    print(f"Documento Word salvo com sucesso em: {docx_output_path}")

if __name__ == "__main__":
    img_path = r"C:\Users\User\Desktop\SubscriptionHub\docs\arquitetura_diagram.png"
    docx_path = r"C:\Users\User\Desktop\SubscriptionHub\docs\arquitetura.docx"
    generate_architecture_diagram(img_path)
    create_docx(img_path, docx_path)
