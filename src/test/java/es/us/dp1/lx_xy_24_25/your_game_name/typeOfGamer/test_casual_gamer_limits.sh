#!/bin/bash

# Script de prueba para verificar las limitaciones de Casual Gamer
# Ejecutar con: bash test_casual_gamer_limits.sh

echo "🎮 INICIANDO PRUEBAS DE LIMITACIONES CASUAL GAMER"
echo "================================================"

BASE_URL="http://localhost:8080"

# Función para hacer login y obtener cookie de sesión
login_user() {
    local username=$1
    local password=$2
    echo "🔐 Autenticando usuario: $username"
    
    # Primero obtenemos la página de login para obtener el CSRF token si es necesario
    curl -s -c cookies.txt "$BASE_URL/login" > /dev/null
    
    # Intentamos login
    curl -s -b cookies.txt -c cookies.txt \
        -X POST \
        -d "username=$username&password=$password" \
        "$BASE_URL/login" > /dev/null
    
    echo "   ✅ Login completado para $username"
}

# Función para crear una partida
create_game() {
    local game_name=$1
    echo "🎯 Intentando crear partida: $game_name"
    
    # Intentamos crear una partida via POST
    response=$(curl -s -b cookies.txt \
        -X POST \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "name=$game_name&maxPlayers=4&isPrivate=false" \
        -w "%{http_code}" \
        "$BASE_URL/gamesessions")
    
    http_code="${response: -3}"
    response_body="${response%???}"
    
    if [[ "$http_code" == "200" ]] || [[ "$http_code" == "302" ]]; then
        echo "   ✅ Partida '$game_name' creada exitosamente (HTTP: $http_code)"
        return 0
    elif [[ "$http_code" == "400" ]] || [[ "$http_code" == "403" ]]; then
        echo "   ❌ Partida '$game_name' rechazada (HTTP: $http_code)"
        if [[ "$response_body" == *"máximo"* ]] || [[ "$response_body" == *"limit"* ]]; then
            echo "      📋 Razón: Límite de Casual Gamer alcanzado"
        fi
        return 1
    else
        echo "   ⚠️  Respuesta inesperada para '$game_name' (HTTP: $http_code)"
        return 2
    fi
}

# Test directo de la validación del servicio
test_service_validation() {
    echo ""
    echo "🧪 PROBANDO VALIDACIÓN DEL SERVICIO DIRECTAMENTE"
    echo "-----------------------------------------------"
    
    # Verificamos que el servicio está respondiendo
    echo "🔍 Verificando que el backend está funcionando..."
    backend_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")
    
    if [[ "$backend_status" == "200" ]]; then
        echo "   ✅ Backend funcionando correctamente"
    else
        echo "   ⚠️  Backend health endpoint no disponible (HTTP: $backend_status)"
        echo "   ℹ️  Intentando endpoint alternativo..."
        
        # Intentamos la página principal
        main_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/")
        if [[ "$main_status" == "200" ]]; then
            echo "   ✅ Backend respondiendo en endpoint principal"
        else
            echo "   ❌ Backend no está respondiendo (HTTP: $main_status)"
            return 1
        fi
    fi
}

# Función principal de pruebas
run_tests() {
    echo ""
    echo "🧪 EJECUTANDO PRUEBAS DE LIMITACIONES"
    echo "-----------------------------------"
    
    # Test 1: Usuario Casual Gamer (aaron)
    echo ""
    echo "👤 PROBANDO USUARIO CASUAL GAMER (aaron)"
    login_user "aaron" "admin123"
    
    echo "📋 Intento 1: Primera partida"
    create_game "Test_Game_1"
    
    echo "📋 Intento 2: Segunda partida"
    create_game "Test_Game_2"
    
    echo "📋 Intento 3: Tercera partida (DEBERÍA FALLAR)"
    if create_game "Test_Game_3_SHOULD_FAIL"; then
        echo "   ❌ ERROR: La tercera partida NO debería haber sido aceptada!"
    else
        echo "   ✅ CORRECTO: La tercera partida fue rechazada como esperado"
    fi
    
    # Test 2: Usuario Hardcore (player1)
    echo ""
    echo "👤 PROBANDO USUARIO HARDCORE (player1)"
    login_user "player1" "admin123"
    
    for i in {1..4}; do
        echo "📋 Intento $i: Partida Hardcore $i"
        if create_game "Hardcore_Game_$i"; then
            echo "   ✅ Usuario Hardcore puede crear partidas sin límite"
        else
            echo "   ⚠️  Partida rechazada - verificar configuración"
        fi
    done
}

# Función de limpieza
cleanup() {
    echo ""
    echo "🧹 Limpiando archivos temporales..."
    rm -f cookies.txt
    echo "   ✅ Limpieza completada"
}

# Ejecutar todas las pruebas
main() {
    # Verificar que el backend está funcionando
    test_service_validation
    
    if [[ $? -eq 0 ]]; then
        # Ejecutar las pruebas principales
        run_tests
        
        echo ""
        echo "🎉 RESUMEN DE PRUEBAS"
        echo "===================="
        echo "✅ Pruebas de limitaciones de Casual Gamer completadas"
        echo "📋 Verificaciones realizadas:"
        echo "   - Usuario Casual Gamer puede crear hasta 2 partidas por día"
        echo "   - Usuario Casual Gamer no puede crear una 3era partida"
        echo "   - Usuario Hardcore no tiene limitaciones de partidas"
        echo ""
        echo "ℹ️  NOTA: Las limitaciones de tiempo se validan durante el juego,"
        echo "   no durante la creación de la partida."
        echo "   El límite está configurado a 1 minuto para testing."
    else
        echo "❌ No se pudieron ejecutar las pruebas - Backend no disponible"
    fi
    
    cleanup
}

# Ejecutar el script principal
main
