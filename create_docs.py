from pathlib import Path

PROJECT_DIR = Path(r"D:\BenimFormum")
INPUT_FILE = PROJECT_DIR / "notebooklm-docs-output.md"
DOCS_DIR = PROJECT_DIR / "docs"

TARGET_FILES = [
    "product-brief.md",
    "architecture.md",
    "data-model.md",
    "ui-flow.md",
    "sprint-plan.md",
    "cursor-prompts.md",
    "decision-log.md",
]


def clean_content(text: str) -> str:
    """
    NotebookLM bazen markdown code block içinde çıktı verebilir.
    Bu fonksiyon gereksiz ```md ve ``` satırlarını temizler.
    """
    lines = text.strip().splitlines()

    if lines and lines[0].strip().startswith("```"):
        lines = lines[1:]

    if lines and lines[-1].strip() == "```":
        lines = lines[:-1]

    return "\n".join(lines).strip() + "\n"


def split_notebooklm_output(raw_text: str) -> dict[str, str]:
    """
    Tek parça NotebookLM çıktısını şu başlıklara göre böler:

    product-brief.md
    architecture.md
    data-model.md
    ui-flow.md
    sprint-plan.md
    cursor-prompts.md
    decision-log.md
    """
    docs: dict[str, list[str]] = {}
    current_file: str | None = None

    for line in raw_text.splitlines():
        stripped = line.strip()

        if stripped in TARGET_FILES:
            current_file = stripped
            docs[current_file] = []
            continue

        if current_file is not None:
            docs[current_file].append(line)

    return {
        file_name: clean_content("\n".join(lines))
        for file_name, lines in docs.items()
    }


def main() -> None:
    if not INPUT_FILE.exists():
        raise FileNotFoundError(
            f"Girdi dosyası bulunamadı: {INPUT_FILE}\n"
            "NotebookLM çıktısını bu dosyaya yapıştırmalısın."
        )

    raw_text = INPUT_FILE.read_text(encoding="utf-8")
    docs = split_notebooklm_output(raw_text)

    DOCS_DIR.mkdir(parents=True, exist_ok=True)

    missing_files = [name for name in TARGET_FILES if name not in docs]

    if missing_files:
        print("UYARI: Şu dosyalar NotebookLM çıktısında bulunamadı:")
        for name in missing_files:
            print(f"- {name}")
        print()

    for file_name in TARGET_FILES:
        if file_name not in docs:
            continue

        output_path = DOCS_DIR / file_name
        output_path.write_text(docs[file_name], encoding="utf-8")
        print(f"OK: {output_path}")

    print()
    print("Tamamlandı.")
    print(f"Üretilen klasör: {DOCS_DIR}")


if __name__ == "__main__":
    main()