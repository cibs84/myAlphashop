-- 1. Elimina gli ingredienti che non hanno un articolo corrispondente
DELETE FROM ingredients 
WHERE codart NOT IN (SELECT codart FROM articles);

-- 2. Ora può aggiungere il vincolo senza errori
ALTER TABLE ingredients
ADD CONSTRAINT fk_ingredients_article
    FOREIGN KEY (codart) 
    REFERENCES articles (codart)
    ON DELETE CASCADE;