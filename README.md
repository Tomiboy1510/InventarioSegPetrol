# Sistema de inventario muy simple hecho para un taller de metalurgia

  - Obtiene el tipo de cambio al Dólar Mayorista, de no ser posible permite ingresarlo manualmente
  - Permite añadir, modificar, eliminar productos (no requiere mayor explicación)
  - Permite actualizar precios sumándoles un porcentaje, de acuerdo a la siguiente fórmula:

        precio <- precio + (porcentaje / 100 * precio)

  - Arrastrar el cursor sobre la tabla para seleccionar varias filas a la vez (para eliminar o para actualizar precios)
  - Permite buscar productos por nombre, buscando nombres que contengan la cadena ingresada, no hace falta que coincidan las mayúsculas (Enter para avanzar de un resultado al siguiente)
  
  - Permite añadir columnas de manera dinámica, cuyo valor se evalúa en base a una expresión matemática
  - Las expresiones pueden contener operadores matemáticos (+,-,/,*), paréntesis, números literales y referencias a otras columnas, y al tipo de cambio
  - Las referencias a columnas tienen el siguiente formato: "COLn", donde n, un número entero, es el identificador de una columna (puede ser tanto positivo como negativo)
  - Al dejar el cursor del mouse sobre el encabezado de una columna se muestra su identificador, y la expresión con la que calcula sus valores (mostrando nombres de columnas en lugar de identificadores para más legibilidad)
  - Para usar el tipo de cambio dentro de una expresión escribir "DOLAR"
  - Tanto para referencias a columnas como al tipo de cambio, usar MAYÚSCULAS
  - Click derecho sobre el encabezado de una columna para eliminarla (También se eliminarán todas las columnas cuya expresión dependa de ella)

  - Ejemplos de expresiones:
  
        COL-4 * COL2 + (2/3)
        COL34 / 10
        (COL13 + 56) * 0.14
        COL-6 * 23,99
