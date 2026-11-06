@echo off
chcp 65001 > nul
echo ================================================================================
echo   SUBSCRIPTION HUB - COMPILACAO DE TODOS OS MICROSSERVICOS E TESTES
echo ================================================================================

if not exist bin mkdir bin

echo [1/3] Mapeando arquivos fontes relativos...
powershell -NoProfile -Command "$sources = Get-ChildItem -Recurse -File -Include '*.java' -Path 'services', 'scripts\src' | ForEach-Object { (Resolve-Path -Relative $_.FullName).Substring(2).Replace('\', '/') }; $utf8 = New-Object System.Text.UTF8Encoding $false; [System.IO.File]::WriteAllLines('sources.txt', $sources, $utf8)"

echo [2/3] Compilando com javac (Java 21)...
javac -encoding UTF-8 -cp "lib/*" -d bin @sources.txt

if %ERRORLEVEL% equ 0 (
    echo [3/3] Compilacao concluida com SUCESSO!
    del sources.txt > nul 2>&1
) else (
    echo [ERRO] Falha na compilacao. Verifique os erros acima.
    del sources.txt > nul 2>&1
    exit /b 1
)

echo ================================================================================
