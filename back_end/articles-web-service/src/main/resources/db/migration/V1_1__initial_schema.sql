--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3 (Debian 16.3-1.pgdg120+1)
-- Dumped by pg_dump version 16.1

-- Started on 2024-08-10 19:22:09 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 16831)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- TOC entry 250 (class 1255 OID 16842)
-- Name: sp_insarticolo(text, text, text, text, text, numeric, numeric, text, numeric); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.sp_insarticolo(IN i_codart text, IN i_descrizione text, IN i_um text, IN i_codstat text, IN i_pzcart text, IN i_pesonetto numeric, IN i_iva numeric, IN i_stato text, IN i_idfamass numeric)
    LANGUAGE plpgsql
    AS $$ 
BEGIN
	
	IF NOT EXISTS(SELECT CODART FROM articoli WHERE CODART = i_codart) THEN
	INSERT INTO articoli
		VALUES(i_codart,i_descrizione,i_um,i_codstat,i_pzcart::smallint,i_pesonetto,i_iva,i_stato,current_date,i_idfamass);
	ELSE
		UPDATE articoli
		SET 
		descrizione = i_descrizione,
		um = i_um,
		codstat = i_codstat,
		pzcart = i_pzcart::smallint,
		pesonetto = i_pesonetto,
		idiva = i_iva,
		idstatoart = i_stato,
		idfamass = i_idfamass
		WHERE codart = i_codart;
	END IF;
	
END;
$$;


--
-- TOC entry 251 (class 1255 OID 16843)
-- Name: sp_inscoupon(text, text); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.sp_inscoupon(IN i_id text, IN i_codfid text)
    LANGUAGE plpgsql
    AS $$ 
BEGIN
	
	IF (i_id = '') THEN
		INSERT INTO coupons
		VALUES('',i_codfid,0,null,null,'No');
	ELSE
		UPDATE coupons
		SET 
		usato = 'Si'
		WHERE id = i_id;
	END IF;
    
END
$$;


--
-- TOC entry 252 (class 1255 OID 16844)
-- Name: sp_inscoupon(text, text, numeric); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.sp_inscoupon(IN i_id text, IN i_codfid text, IN i_valore numeric)
    LANGUAGE plpgsql
    AS $$ 
BEGIN
	
	IF (i_id = '') THEN
		INSERT INTO coupons
		VALUES('',i_codfid,0,null,null,'No');
	ELSE
		UPDATE coupons
		SET 
		usato = 'Si'
		WHERE id = i_id;
	END IF;
    
END;
$$;


--
-- TOC entry 253 (class 1255 OID 16845)
-- Name: tr_inserisci_coupon(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.tr_inserisci_coupon() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE fatturato NUMERIC;
BEGIN
	SELECT INTO fatturato SUM(totale) FROM scontrini WHERE codfid = NEW.codfid;
	
	IF (fatturato >= 200) THEN
		NEW.valore := 20;
	ELSIF (fatturato >= 100 AND fatturato < 200) THEN
		NEW.valore := 10;
	ELSE
		NEW.valore := 5;
	END IF;
	
	NEW.id := uuid_generate_v4();
	NEW.scadenza := current_date + 15;
	RETURN NEW;
	
END;
$$;


--
-- TOC entry 254 (class 1255 OID 16846)
-- Name: tr_modifica_coupons(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.tr_modifica_coupons() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	
	RAISE INFO 'Giorni Disponibili: %,%',OLD.scadenza,current_date;  
	 
	IF (OLD.scadenza >= current_date AND NEW.usato = 'Si') THEN
		NEW.datamodifica := current_date;
	ELSE
		NEW.usato := 'No';
	END IF;
		
	RETURN NEW;

END;
$$;


--
-- TOC entry 255 (class 1255 OID 16847)
-- Name: uf_getqtamag(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.uf_getqtamag(v_codart character varying) RETURNS numeric
    LANGUAGE plpgsql STRICT
    AS $$
DECLARE V_RetVal NUMERIC; 
BEGIN
	 SELECT (ACQUISTATO - RESO - VENDUTO - USCITE - SCADUTI) INTO V_RetVal
     FROM MOVIMENTI
     WHERE
     CODART = V_CodArt;  
	 
	 IF V_RetVal < 0 THEN
		RAISE NO_DATA_FOUND;
	ELSE
		RETURN V_RetVal;
	END IF;
	
	EXCEPTION
	WHEN NO_DATA_FOUND
    THEN
		RAISE INFO 'AVVISO: Valore di Magazzino dell''articolo %s minore di 0',V_CodArt;
        RETURN 0;
END;
$$;


--
-- TOC entry 216 (class 1259 OID 16848)
-- Name: articles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.articles (
    codart character varying(20) NOT NULL,
    description character varying(60) DEFAULT NULL::character varying,
    um character(2) DEFAULT NULL::bpchar,
    codstat character varying(20) DEFAULT NULL::character varying,
    pzcart smallint,
    netweight double precision,
    idvat integer,
    idartstatus character(1) DEFAULT NULL::bpchar,
    creationdate date,
    idfamass integer
);


--
-- TOC entry 256 (class 1255 OID 16855)
-- Name: uf_selarticolo(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.uf_selarticolo(codart_i character varying) RETURNS SETOF public.articles
    LANGUAGE plpgsql
    AS $$
BEGIN
	RETURN QUERY 
	(
		SELECT
		*
		FROM ARTICOLI
		WHERE CODART = CODART_I
	);
END;
$$;


--
-- TOC entry 257 (class 1255 OID 16856)
-- Name: uf_selarticolo2(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.uf_selarticolo2(v_codart character varying) RETURNS TABLE(articolo text, um character, pzcart smallint, codstat character varying, peso double precision, iva integer, stato text, famass text, magazzino numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
	RETURN QUERY
	(
		SELECT
		A.CODART || ' ' || A.DESCRIZIONE,
		A.UM,
		A.PZCART,
		A.CODSTAT,
		A.PESONETTO,
		A.IDIVA AS IVA,
		CASE WHEN A.IDSTATOART = '1' THEN 'ATTIVO' ELSE 'NON ATTIVO' END AS STATO,
		A.IDFAMASS || ' ' || TRIM(B.DESCRIZIONE) AS REPARTO,
		Uf_GetQtaMag(A.CODART) AS QtaMag
		FROM ARTICOLI A JOIN FAMASSORT B
		ON A.IDFAMASS = B.ID
		WHERE A.CODART = V_CodArt
	);
END;
$$;


--
-- TOC entry 217 (class 1259 OID 16857)
-- Name: barcode; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.barcode (
    codart character varying(20) NOT NULL,
    barcode character varying(13) NOT NULL,
    idtypeart character varying(2) NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 16860)
-- Name: cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cards (
    codfidelity character varying(20) NOT NULL,
    stickers integer,
    lastexpense date,
    obsolete character varying(50) DEFAULT NULL::character varying
);


--
-- TOC entry 219 (class 1259 OID 16864)
-- Name: classcr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.classcr (
    codart character varying(20) NOT NULL,
    liv1 character(2) NOT NULL,
    liv2 character(3) NOT NULL,
    liv3 character(3) DEFAULT NULL::bpchar
);


--
-- TOC entry 220 (class 1259 OID 16868)
-- Name: coupons; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.coupons (
    id character varying(50) NOT NULL,
    codfid character varying(20),
    value double precision,
    expiration date,
    modificationdate date,
    used character(2)
);


--
-- TOC entry 221 (class 1259 OID 16871)
-- Name: customers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.customers (
    codfidelity character varying(20) NOT NULL,
    name character varying(50) DEFAULT NULL::character varying,
    surname character varying(60) DEFAULT NULL::character varying,
    address character varying(80) DEFAULT NULL::character varying,
    city character varying(50) DEFAULT NULL::character varying,
    zipcode character varying(6) DEFAULT NULL::character varying,
    prov character varying(3) DEFAULT NULL::character varying,
    telephone character varying(30) DEFAULT NULL::character varying,
    mail character varying(30) DEFAULT NULL::character varying,
    country character varying(2) DEFAULT NULL::character varying,
    creationdate date
);


--
-- TOC entry 222 (class 1259 OID 16883)
-- Name: deposits; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.deposits (
    id integer NOT NULL,
    codcedi character varying(10) NOT NULL,
    description character varying(50) NOT NULL,
    address character varying(80) DEFAULT NULL::character varying,
    zipcode character(5) DEFAULT NULL::bpchar,
    city character varying(50) DEFAULT NULL::character varying,
    prov character(2) DEFAULT NULL::bpchar,
    country character varying(20) DEFAULT NULL::character varying
);


--
-- TOC entry 223 (class 1259 OID 16891)
-- Name: deprifpromo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.deprifpromo (
    id bigint NOT NULL,
    idpromo character varying(255) NOT NULL,
    iddeposit integer NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16894)
-- Name: detlists; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detlists (
    id integer NOT NULL,
    codart character varying(20) NOT NULL,
    idlist character varying(10) NOT NULL,
    price numeric(8,2) NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 16897)
-- Name: detorders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detorders (
    id bigint NOT NULL,
    idorder character varying(255) NOT NULL,
    codart character varying(20),
    qty double precision,
    price double precision
);


--
-- TOC entry 226 (class 1259 OID 16900)
-- Name: detpromo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detpromo (
    id bigint NOT NULL,
    idpromo character varying(255) NOT NULL,
    "row" smallint NOT NULL,
    codart character varying(20) NOT NULL,
    codfid character varying(20) DEFAULT NULL::character varying,
    start date,
    "end" date,
    idtypepromo smallint NOT NULL,
    object character varying(20) NOT NULL,
    isfid character(2) DEFAULT NULL::bpchar
);


--
-- TOC entry 227 (class 1259 OID 16905)
-- Name: detreceipts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detreceipts (
    id character varying(40) NOT NULL,
    idreceipt character varying(40) NOT NULL,
    "row" smallint NOT NULL,
    barcode character(13) DEFAULT NULL::bpchar,
    codart character varying(20) DEFAULT NULL::character varying,
    qty double precision NOT NULL,
    qtycounter smallint NOT NULL,
    discount double precision,
    price double precision NOT NULL,
    selling double precision,
    inpromo character(2) DEFAULT NULL::bpchar
);


--
-- TOC entry 228 (class 1259 OID 16911)
-- Name: ecrliv1; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ecrliv1 (
    id character(2) NOT NULL,
    description character varying(30) NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 16914)
-- Name: ecrliv2; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ecrliv2 (
    id character(3) NOT NULL,
    description character varying(50) NOT NULL,
    idliv1 character(2) NOT NULL
);


--
-- TOC entry 230 (class 1259 OID 16917)
-- Name: famassort; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.famassort (
    id integer NOT NULL,
    description character varying(60) NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 16920)
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 14343
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 232 (class 1259 OID 16921)
-- Name: ingredients; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ingredients (
    codart character varying(20) NOT NULL,
    info character varying(300) NOT NULL
);


--
-- TOC entry 233 (class 1259 OID 16924)
-- Name: lists; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lists (
    id character varying(10) NOT NULL,
    description character varying(30) NOT NULL,
    obsolete character(2) NOT NULL
);


--
-- TOC entry 234 (class 1259 OID 16927)
-- Name: movements; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movements (
    codart character varying(20) NOT NULL,
    desart character varying(50) NOT NULL,
    pricepurchase double precision,
    purchased double precision NOT NULL,
    returned double precision,
    sold double precision,
    releases double precision,
    expired double precision
);


--
-- TOC entry 235 (class 1259 OID 16930)
-- Name: orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.orders (
    id character varying(255) NOT NULL,
    date date NOT NULL,
    idpdv integer,
    codfid character varying(20) NOT NULL,
    status smallint NOT NULL
);


--
-- TOC entry 236 (class 1259 OID 16933)
-- Name: promo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.promo (
    idpromo character varying(255) NOT NULL,
    year smallint NOT NULL,
    code character(10) NOT NULL,
    description character varying(50) DEFAULT NULL::character varying
);


--
-- TOC entry 237 (class 1259 OID 16937)
-- Name: receipts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.receipts (
    id character varying(40) NOT NULL,
    date date NOT NULL,
    iddeposit integer NOT NULL,
    counter smallint NOT NULL,
    receipt integer NOT NULL,
    codfid character varying(20) DEFAULT NULL::character varying,
    stickers integer NOT NULL,
    "time" time without time zone NOT NULL,
    total double precision
);


--
-- TOC entry 238 (class 1259 OID 16941)
-- Name: typepromo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.typepromo (
    idtipopromo smallint NOT NULL,
    descrizione character varying(50) DEFAULT NULL::character varying
);


--
-- TOC entry 239 (class 1259 OID 16945)
-- Name: vat; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.vat (
    idvat integer NOT NULL,
    description character varying(30) NOT NULL,
    taxrate smallint NOT NULL
);


--
-- TOC entry 3348 (class 2606 OID 16949)
-- Name: coupons Coupons_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT "Coupons_pkey" PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 16951)
-- Name: articles articoli_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.articles
    ADD CONSTRAINT articoli_pkey PRIMARY KEY (codart);


--
-- TOC entry 3342 (class 2606 OID 16953)
-- Name: barcode barcode_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barcode
    ADD CONSTRAINT barcode_pkey PRIMARY KEY (barcode);


--
-- TOC entry 3344 (class 2606 OID 16955)
-- Name: cards cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (codfidelity);


--
-- TOC entry 3346 (class 2606 OID 16957)
-- Name: classcr classecr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.classcr
    ADD CONSTRAINT classecr_pkey PRIMARY KEY (codart);


--
-- TOC entry 3350 (class 2606 OID 16959)
-- Name: customers clienti_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT clienti_pkey PRIMARY KEY (codfidelity);


--
-- TOC entry 3353 (class 2606 OID 16961)
-- Name: deposits depositi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deposits
    ADD CONSTRAINT depositi_pkey PRIMARY KEY (id);


--
-- TOC entry 3355 (class 2606 OID 16963)
-- Name: deprifpromo deprifpromo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deprifpromo
    ADD CONSTRAINT deprifpromo_pkey PRIMARY KEY (id);


--
-- TOC entry 3357 (class 2606 OID 16965)
-- Name: detlists dettlistini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detlists
    ADD CONSTRAINT dettlistini_pkey PRIMARY KEY (id);


--
-- TOC entry 3359 (class 2606 OID 16967)
-- Name: detorders dettordini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detorders
    ADD CONSTRAINT dettordini_pkey PRIMARY KEY (id);


--
-- TOC entry 3361 (class 2606 OID 16969)
-- Name: detpromo dettpromo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detpromo
    ADD CONSTRAINT dettpromo_pkey PRIMARY KEY (id);


--
-- TOC entry 3363 (class 2606 OID 16971)
-- Name: detreceipts dettscontrini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detreceipts
    ADD CONSTRAINT dettscontrini_pkey PRIMARY KEY (id);


--
-- TOC entry 3365 (class 2606 OID 16973)
-- Name: ecrliv1 ecrliv1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ecrliv1
    ADD CONSTRAINT ecrliv1_pkey PRIMARY KEY (id);


--
-- TOC entry 3367 (class 2606 OID 16975)
-- Name: ecrliv2 ecrliv2_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ecrliv2
    ADD CONSTRAINT ecrliv2_pkey PRIMARY KEY (id, idliv1);


--
-- TOC entry 3369 (class 2606 OID 16977)
-- Name: famassort famassort_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.famassort
    ADD CONSTRAINT famassort_pkey PRIMARY KEY (id);


--
-- TOC entry 3371 (class 2606 OID 16979)
-- Name: ingredients ingredienti_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ingredients
    ADD CONSTRAINT ingredienti_pkey PRIMARY KEY (codart);


--
-- TOC entry 3385 (class 2606 OID 16981)
-- Name: vat iva_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vat
    ADD CONSTRAINT iva_pkey PRIMARY KEY (idvat);


--
-- TOC entry 3373 (class 2606 OID 16983)
-- Name: lists listini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lists
    ADD CONSTRAINT listini_pkey PRIMARY KEY (id);


--
-- TOC entry 3375 (class 2606 OID 16985)
-- Name: movements movimenti_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movements
    ADD CONSTRAINT movimenti_pkey PRIMARY KEY (codart);


--
-- TOC entry 3377 (class 2606 OID 16987)
-- Name: orders ordini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT ordini_pkey PRIMARY KEY (id);


--
-- TOC entry 3379 (class 2606 OID 16989)
-- Name: promo promo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.promo
    ADD CONSTRAINT promo_pkey PRIMARY KEY (idpromo);


--
-- TOC entry 3381 (class 2606 OID 16991)
-- Name: receipts scontrini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.receipts
    ADD CONSTRAINT scontrini_pkey PRIMARY KEY (id);


--
-- TOC entry 3383 (class 2606 OID 16993)
-- Name: typepromo tipopromo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.typepromo
    ADD CONSTRAINT tipopromo_pkey PRIMARY KEY (idtipopromo);


--
-- TOC entry 3351 (class 1259 OID 16994)
-- Name: idx_cognome; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cognome ON public.customers USING btree (surname);


--
-- TOC entry 3340 (class 1259 OID 16995)
-- Name: idx_descrizione; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_descrizione ON public.articles USING btree (description);


--
-- TOC entry 3395 (class 2620 OID 16996)
-- Name: coupons tr_inserisci_coupon; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_inserisci_coupon BEFORE INSERT ON public.coupons FOR EACH ROW EXECUTE FUNCTION public.tr_inserisci_coupon();


--
-- TOC entry 3396 (class 2620 OID 16997)
-- Name: coupons tr_modifica_coupons; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_modifica_coupons BEFORE UPDATE ON public.coupons FOR EACH ROW EXECUTE FUNCTION public.tr_modifica_coupons();


--
-- TOC entry 3388 (class 2606 OID 16998)
-- Name: barcode articoli_fk_barcode; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barcode
    ADD CONSTRAINT articoli_fk_barcode FOREIGN KEY (codart) REFERENCES public.articles(codart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3390 (class 2606 OID 17003)
-- Name: classcr articoli_fk_classecr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.classcr
    ADD CONSTRAINT articoli_fk_classecr FOREIGN KEY (codart) REFERENCES public.articles(codart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3392 (class 2606 OID 17008)
-- Name: detlists articoli_fk_dettlistini; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detlists
    ADD CONSTRAINT articoli_fk_dettlistini FOREIGN KEY (codart) REFERENCES public.articles(codart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3389 (class 2606 OID 17013)
-- Name: cards clienti_fk_cards; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT clienti_fk_cards FOREIGN KEY (codfidelity) REFERENCES public.customers(codfidelity);


--
-- TOC entry 3386 (class 2606 OID 17018)
-- Name: articles famassort_fk_articoli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.articles
    ADD CONSTRAINT famassort_fk_articoli FOREIGN KEY (idfamass) REFERENCES public.famassort(id) ON UPDATE CASCADE;


--
-- TOC entry 3391 (class 2606 OID 17023)
-- Name: deprifpromo fk_deprifpromo_promo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deprifpromo
    ADD CONSTRAINT fk_deprifpromo_promo FOREIGN KEY (idpromo) REFERENCES public.promo(idpromo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3393 (class 2606 OID 17028)
-- Name: detlists fk_dettlistini_listini; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detlists
    ADD CONSTRAINT fk_dettlistini_listini FOREIGN KEY (idlist) REFERENCES public.lists(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3394 (class 2606 OID 17033)
-- Name: detpromo fk_dettpromo_promo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detpromo
    ADD CONSTRAINT fk_dettpromo_promo FOREIGN KEY (idpromo) REFERENCES public.promo(idpromo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3387 (class 2606 OID 17038)
-- Name: articles iva_fk_articoli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.articles
    ADD CONSTRAINT iva_fk_articoli FOREIGN KEY (idvat) REFERENCES public.vat(idvat);


-- Completed on 2024-08-10 19:22:09 UTC

--
-- PostgreSQL database dump complete
--

