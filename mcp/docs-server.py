from pathlib import Path
from mcp.server.fastmcp import FastMCP

mcp = FastMCP("Benim Formum Docs MCP")

PROJECT_ROOT = Path(r"D:\BenimFormum")
DOCS_DIR = PROJECT_ROOT / "docs"

ALLOWED_EXTENSIONS = {".md", ".txt"}


def get_docs() -> list[Path]:
    if not DOCS_DIR.exists():
        return []

    return [
        path for path in DOCS_DIR.rglob("*")
        if path.is_file() and path.suffix.lower() in ALLOWED_EXTENSIONS
    ]


def safe_doc_path(file_name: str) -> Path:
    target = (DOCS_DIR / file_name).resolve()

    if not str(target).startswith(str(DOCS_DIR.resolve())):
        raise ValueError("docs klasörü dışına erişim engellendi.")

    if target.suffix.lower() not in ALLOWED_EXTENSIONS:
        raise ValueError("Sadece .md ve .txt dosyaları okunabilir.")

    if not target.exists():
        raise FileNotFoundError(f"Dosya bulunamadı: {file_name}")

    return target


@mcp.tool()
def list_docs() -> str:
    """Benim Formum docs klasöründeki dokümanları listeler."""
    docs = get_docs()

    if not docs:
        return "docs klasöründe doküman bulunamadı."

    return "\n".join(str(doc.relative_to(DOCS_DIR)) for doc in docs)


@mcp.tool()
def read_doc(file_name: str) -> str:
    """Belirli bir docs dosyasını okur. Örnek: read_doc('architecture.md')"""
    path = safe_doc_path(file_name)
    return path.read_text(encoding="utf-8", errors="ignore")


@mcp.tool()
def search_docs(query: str, max_results: int = 5) -> str:
    """docs klasöründe basit metin araması yapar."""
    query = query.lower().strip()

    if not query:
        return "Arama sorgusu boş olamaz."

    results = []

    for doc in get_docs():
        text = doc.read_text(encoding="utf-8", errors="ignore")
        lower_text = text.lower()

        score = lower_text.count(query)

        query_words = [word for word in query.split() if len(word) > 2]
        for word in query_words:
            score += lower_text.count(word)

        if score > 0:
            first_word = query_words[0] if query_words else query
            index = lower_text.find(first_word)

            if index == -1:
                index = 0

            start = max(0, index - 500)
            end = min(len(text), index + 1500)
            snippet = text[start:end].strip()

            results.append({
                "file": str(doc.relative_to(DOCS_DIR)),
                "score": score,
                "snippet": snippet
            })

    results.sort(key=lambda item: item["score"], reverse=True)
    results = results[:max_results]

    if not results:
        return f"'{query}' için sonuç bulunamadı."

    output = []

    for item in results:
        output.append(
            f"FILE: {item['file']}\n"
            f"SCORE: {item['score']}\n"
            f"SNIPPET:\n{item['snippet']}\n"
            f"{'-' * 80}"
        )

    return "\n\n".join(output)


@mcp.tool()
def get_project_brief() -> str:
    """Ana proje dokümanlarını birleştirerek kısa proje bağlamı verir."""
    files = [
        "product-brief.md",
        "architecture.md",
        "data-model.md",
        "sprint-plan.md",
        "decision-log.md",
    ]

    parts = []

    for file_name in files:
        path = DOCS_DIR / file_name
        if path.exists():
            parts.append(
                f"# {file_name}\n\n"
                f"{path.read_text(encoding='utf-8', errors='ignore')}"
            )

    if not parts:
        return "Proje brief dosyaları bulunamadı."

    return "\n\n---\n\n".join(parts)


@mcp.tool()
def get_architecture_rules() -> str:
    """architecture.md dosyasını döndürür."""
    path = DOCS_DIR / "architecture.md"

    if not path.exists():
        return "architecture.md bulunamadı."

    return path.read_text(encoding="utf-8", errors="ignore")


@mcp.tool()
def get_sprint_plan() -> str:
    """sprint-plan.md dosyasını döndürür."""
    path = DOCS_DIR / "sprint-plan.md"

    if not path.exists():
        return "sprint-plan.md bulunamadı."

    return path.read_text(encoding="utf-8", errors="ignore")


if __name__ == "__main__":
    mcp.run(transport="stdio")