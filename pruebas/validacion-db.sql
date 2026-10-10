-- Pruebas de validación de bd_archivos (PostgreSQL)
-- Todo corre dentro de una transacción que se revierte: no deja datos de prueba.
\set ON_ERROR_STOP on
\pset footer off
\set QUIET on
\o /dev/null
BEGIN;

CREATE TEMP TABLE resultado (n serial, prueba text, ok boolean, detalle text) ON COMMIT DROP;

CREATE FUNCTION pg_temp.chk(p text, c boolean, d text DEFAULT '') RETURNS void LANGUAGE sql AS
$$ INSERT INTO resultado (prueba, ok, detalle) VALUES (p, c, d) $$;

-- 1. Estructura
SELECT pg_temp.chk('Existe tabla usuario', to_regclass('public.usuario') IS NOT NULL);
SELECT pg_temp.chk('Existe tabla historia_clinica', to_regclass('public.historia_clinica') IS NOT NULL);
SELECT pg_temp.chk('historia_clinica tiene 8 columnas',
  (SELECT count(*) FROM information_schema.columns WHERE table_name='historia_clinica') = 8);
SELECT pg_temp.chk('Usuario ADMIN (id 1) creado', EXISTS (SELECT 1 FROM usuario WHERE id_usuario=1));
SELECT pg_temp.chk('Índices creados',
  (SELECT count(*) FROM pg_indexes WHERE tablename='historia_clinica' AND indexname LIKE 'ix_hc_%') = 2);
SELECT pg_temp.chk('Zona horaria America/Lima', current_setting('TimeZone') = 'America/Lima', current_setting('TimeZone'));

-- 2. Inserción y valores por defecto
INSERT INTO historia_clinica (id_usuario,dni,codigo_hc,apellido_paterno,apellido_materno,nombres)
VALUES (1,'90000001','Z001','ZAPATA','LOPEZ','CARLOS'),
       (1,'90000002','Z009','ZEGARRA',NULL,'ANA'),
       (1,'90000003','Z010','ZUÑIGA','RUIZ','LUIS');
SELECT pg_temp.chk('Inserta 3 historias', (SELECT count(*) FROM historia_clinica WHERE dni LIKE '9000000_') = 3);
SELECT pg_temp.chk('fecha_registro se llena sola',
  (SELECT bool_and(fecha_registro IS NOT NULL) FROM historia_clinica WHERE dni LIKE '9000000_'));
SELECT pg_temp.chk('id_historia autoincremental',
  (SELECT count(DISTINCT id_historia) FROM historia_clinica WHERE dni LIKE '9000000_') = 3);
SELECT pg_temp.chk('Acepta Ñ (UTF-8)', EXISTS (SELECT 1 FROM historia_clinica WHERE apellido_paterno='ZUÑIGA'));

-- 3. Consulta del correlativo (igual a obtenerUltimoCodigoPorLetra)
SELECT pg_temp.chk('Último código Z es Z010 (no Z009)',
  (SELECT codigo_hc FROM historia_clinica WHERE codigo_hc LIKE 'Z%'
   ORDER BY length(codigo_hc) DESC, codigo_hc DESC FETCH FIRST 1 ROWS ONLY) = 'Z010');

-- 4. Búsqueda por nombre (igual a buscarPorNombreCompleto)
CREATE TEMP TABLE r_b (k text, c bigint);
INSERT INTO r_b SELECT 'zap', count(*) FROM historia_clinica WHERE dni LIKE '9000000_' AND lower(apellido_paterno) LIKE '%zap%';
SELECT pg_temp.chk('Búsqueda por apellido (minúsculas) encuentra 1', (SELECT c FROM r_b WHERE k='zap') = 1);

-- 5. Restricciones (cada una debe FALLAR)
DO $$
BEGIN
  BEGIN INSERT INTO historia_clinica (id_usuario,dni,codigo_hc,apellido_paterno,nombres) VALUES (1,'90000001','Z099','X','Y');
        PERFORM pg_temp.chk('Rechaza DNI duplicado', false, 'se insertó');
  EXCEPTION WHEN unique_violation THEN PERFORM pg_temp.chk('Rechaza DNI duplicado', true); END;
  BEGIN INSERT INTO historia_clinica (id_usuario,dni,codigo_hc,apellido_paterno,nombres) VALUES (1,'90000099','Z001','X','Y');
        PERFORM pg_temp.chk('Rechaza código HC duplicado', false, 'se insertó');
  EXCEPTION WHEN unique_violation THEN PERFORM pg_temp.chk('Rechaza código HC duplicado', true); END;
  BEGIN INSERT INTO historia_clinica (id_usuario,dni,codigo_hc,apellido_paterno,nombres) VALUES (999999,'90000098','Z098','X','Y');
        PERFORM pg_temp.chk('Rechaza usuario inexistente (FK)', false, 'se insertó');
  EXCEPTION WHEN foreign_key_violation THEN PERFORM pg_temp.chk('Rechaza usuario inexistente (FK)', true); END;
  BEGIN INSERT INTO historia_clinica (id_usuario,dni,codigo_hc,nombres) VALUES (1,'90000097','Z097','Y');
        PERFORM pg_temp.chk('Rechaza apellido paterno vacío', false, 'se insertó');
  EXCEPTION WHEN not_null_violation THEN PERFORM pg_temp.chk('Rechaza apellido paterno vacío', true); END;
  BEGIN INSERT INTO historia_clinica (id_usuario,dni,codigo_hc,apellido_paterno,nombres) VALUES (1,'123456789012345678901','Z096','X','Y');
        PERFORM pg_temp.chk('Rechaza DNI de más de 20 caracteres', false, 'se insertó');
  EXCEPTION WHEN string_data_right_truncation THEN PERFORM pg_temp.chk('Rechaza DNI de más de 20 caracteres', true); END;
END $$;

\o
SELECT n AS "#", prueba, CASE WHEN ok THEN 'OK' ELSE 'FALLA' END AS resultado, detalle FROM resultado ORDER BY n;
SELECT count(*) FILTER (WHERE ok) || ' de ' || count(*) || ' pruebas OK' AS resumen FROM resultado;

ROLLBACK;
