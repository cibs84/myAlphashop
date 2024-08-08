--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3 (Debian 16.3-1.pgdg120+1)
-- Dumped by pg_dump version 16.1

-- Started on 2024-08-07 22:32:04 UTC

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
-- TOC entry 2 (class 3079 OID 16611)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- TOC entry 250 (class 1255 OID 16622)
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
-- TOC entry 251 (class 1255 OID 16623)
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
-- TOC entry 252 (class 1255 OID 16624)
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
-- TOC entry 253 (class 1255 OID 16625)
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
-- TOC entry 254 (class 1255 OID 16626)
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
-- TOC entry 255 (class 1255 OID 16627)
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
-- TOC entry 216 (class 1259 OID 16628)
-- Name: articoli; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.articoli (
    codart character varying(20) NOT NULL,
    descrizione character varying(60) DEFAULT NULL::character varying,
    um character(2) DEFAULT NULL::bpchar,
    codstat character varying(20) DEFAULT NULL::character varying,
    pzcart smallint,
    pesonetto double precision,
    idiva integer,
    idstatoart character(1) DEFAULT NULL::bpchar,
    datacreazione date,
    idfamass integer
);


--
-- TOC entry 256 (class 1255 OID 16635)
-- Name: uf_selarticolo(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.uf_selarticolo(codart_i character varying) RETURNS SETOF public.articoli
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
-- TOC entry 257 (class 1255 OID 16636)
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
-- TOC entry 217 (class 1259 OID 16637)
-- Name: barcode; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.barcode (
    codart character varying(20) NOT NULL,
    barcode character varying(13) NOT NULL,
    idtipoart character varying(2) NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 16640)
-- Name: cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cards (
    codfidelity character varying(20) NOT NULL,
    bollini integer,
    ultimaspesa date,
    obsoleto character varying(50) DEFAULT NULL::character varying
);


--
-- TOC entry 219 (class 1259 OID 16644)
-- Name: classecr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.classecr (
    codart character varying(20) NOT NULL,
    liv1 character(2) NOT NULL,
    liv2 character(3) NOT NULL,
    liv3 character(3) DEFAULT NULL::bpchar
);


--
-- TOC entry 220 (class 1259 OID 16648)
-- Name: clienti; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clienti (
    codfidelity character varying(20) NOT NULL,
    nome character varying(50) DEFAULT NULL::character varying,
    cognome character varying(60) DEFAULT NULL::character varying,
    indirizzo character varying(80) DEFAULT NULL::character varying,
    comune character varying(50) DEFAULT NULL::character varying,
    cap character varying(6) DEFAULT NULL::character varying,
    prov character varying(3) DEFAULT NULL::character varying,
    telefono character varying(30) DEFAULT NULL::character varying,
    mail character varying(30) DEFAULT NULL::character varying,
    stato character varying(2) DEFAULT NULL::character varying,
    datacreaz date
);


--
-- TOC entry 221 (class 1259 OID 16660)
-- Name: coupons; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.coupons (
    id character varying(50) NOT NULL,
    codfid character varying(20),
    valore double precision,
    scadenza date,
    datamodifica date,
    usato character(2)
);


--
-- TOC entry 222 (class 1259 OID 16663)
-- Name: depositi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.depositi (
    id integer NOT NULL,
    codcedi character varying(10) NOT NULL,
    descrizione character varying(50) NOT NULL,
    indirizzo character varying(80) DEFAULT NULL::character varying,
    cap character(5) DEFAULT NULL::bpchar,
    comune character varying(50) DEFAULT NULL::character varying,
    prov character(2) DEFAULT NULL::bpchar,
    stato character varying(20) DEFAULT NULL::character varying
);


--
-- TOC entry 223 (class 1259 OID 16671)
-- Name: deprifpromo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.deprifpromo (
    id bigint NOT NULL,
    idpromo character varying(255) NOT NULL,
    iddeposito integer NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16674)
-- Name: dettlistini; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dettlistini (
    id integer NOT NULL,
    codart character varying(20) NOT NULL,
    idlist character varying(10) NOT NULL,
    prezzo numeric(8,2) NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 16677)
-- Name: dettordini; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dettordini (
    id bigint NOT NULL,
    idordine character varying(255) NOT NULL,
    codart character varying(20),
    qta double precision,
    prezzo double precision
);


--
-- TOC entry 226 (class 1259 OID 16680)
-- Name: dettpromo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dettpromo (
    id bigint NOT NULL,
    idpromo character varying(255) NOT NULL,
    riga smallint NOT NULL,
    codart character varying(20) NOT NULL,
    codfid character varying(20) DEFAULT NULL::character varying,
    inizio date,
    fine date,
    idtipopromo smallint NOT NULL,
    oggetto character varying(20) NOT NULL,
    isfid character(2) DEFAULT NULL::bpchar
);


--
-- TOC entry 227 (class 1259 OID 16685)
-- Name: dettscontrini; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dettscontrini (
    id character varying(40) NOT NULL,
    idscontrino character varying(40) NOT NULL,
    riga smallint NOT NULL,
    barcode character(13) DEFAULT NULL::bpchar,
    codart character varying(20) DEFAULT NULL::character varying,
    qta double precision NOT NULL,
    qtacassa smallint NOT NULL,
    sconto double precision,
    prezzo double precision NOT NULL,
    vendita double precision,
    inpromo character(2) DEFAULT NULL::bpchar
);


--
-- TOC entry 228 (class 1259 OID 16691)
-- Name: ecrliv1; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ecrliv1 (
    id character(2) NOT NULL,
    descrizione character varying(30) NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 16694)
-- Name: ecrliv2; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ecrliv2 (
    id character(3) NOT NULL,
    descrizione character varying(50) NOT NULL,
    idliv1 character(2) NOT NULL
);


--
-- TOC entry 230 (class 1259 OID 16697)
-- Name: famassort; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.famassort (
    id integer NOT NULL,
    descrizione character varying(60) NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 16700)
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 14343
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 232 (class 1259 OID 16701)
-- Name: ingredienti; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ingredienti (
    codart character varying(20) NOT NULL,
    info character varying(300) NOT NULL
);


--
-- TOC entry 233 (class 1259 OID 16704)
-- Name: iva; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.iva (
    idiva integer NOT NULL,
    descrizione character varying(30) NOT NULL,
    aliquota smallint NOT NULL
);


--
-- TOC entry 234 (class 1259 OID 16707)
-- Name: listini; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.listini (
    id character varying(10) NOT NULL,
    descrizione character varying(30) NOT NULL,
    obsoleto character(2) NOT NULL
);


--
-- TOC entry 235 (class 1259 OID 16710)
-- Name: movimenti; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movimenti (
    codart character varying(20) NOT NULL,
    desart character varying(50) NOT NULL,
    przacq double precision,
    acquistato double precision NOT NULL,
    reso double precision,
    venduto double precision,
    uscite double precision,
    scaduti double precision
);


--
-- TOC entry 236 (class 1259 OID 16713)
-- Name: ordini; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ordini (
    id character varying(255) NOT NULL,
    data date NOT NULL,
    idpdv integer,
    codfid character varying(20) NOT NULL,
    stato smallint NOT NULL
);


--
-- TOC entry 237 (class 1259 OID 16716)
-- Name: promo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.promo (
    idpromo character varying(255) NOT NULL,
    anno smallint NOT NULL,
    codice character(10) NOT NULL,
    descrizione character varying(50) DEFAULT NULL::character varying
);


--
-- TOC entry 238 (class 1259 OID 16720)
-- Name: scontrini; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.scontrini (
    id character varying(40) NOT NULL,
    data date NOT NULL,
    iddeposito integer NOT NULL,
    cassa smallint NOT NULL,
    scontrino integer NOT NULL,
    codfid character varying(20) DEFAULT NULL::character varying,
    bollini integer NOT NULL,
    ora time without time zone NOT NULL,
    totale double precision
);


--
-- TOC entry 239 (class 1259 OID 16724)
-- Name: tipopromo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tipopromo (
    idtipopromo smallint NOT NULL,
    descrizione character varying(50) DEFAULT NULL::character varying
);


--
-- TOC entry 3351 (class 2606 OID 16729)
-- Name: coupons Coupons_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT "Coupons_pkey" PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 16731)
-- Name: articoli articoli_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.articoli
    ADD CONSTRAINT articoli_pkey PRIMARY KEY (codart);


--
-- TOC entry 3342 (class 2606 OID 16733)
-- Name: barcode barcode_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barcode
    ADD CONSTRAINT barcode_pkey PRIMARY KEY (barcode);


--
-- TOC entry 3344 (class 2606 OID 16735)
-- Name: cards cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (codfidelity);


--
-- TOC entry 3346 (class 2606 OID 16737)
-- Name: classecr classecr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.classecr
    ADD CONSTRAINT classecr_pkey PRIMARY KEY (codart);


--
-- TOC entry 3348 (class 2606 OID 16739)
-- Name: clienti clienti_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clienti
    ADD CONSTRAINT clienti_pkey PRIMARY KEY (codfidelity);


--
-- TOC entry 3353 (class 2606 OID 16741)
-- Name: depositi depositi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.depositi
    ADD CONSTRAINT depositi_pkey PRIMARY KEY (id);


--
-- TOC entry 3355 (class 2606 OID 16743)
-- Name: deprifpromo deprifpromo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deprifpromo
    ADD CONSTRAINT deprifpromo_pkey PRIMARY KEY (id);


--
-- TOC entry 3357 (class 2606 OID 16745)
-- Name: dettlistini dettlistini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettlistini
    ADD CONSTRAINT dettlistini_pkey PRIMARY KEY (id);


--
-- TOC entry 3359 (class 2606 OID 16747)
-- Name: dettordini dettordini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettordini
    ADD CONSTRAINT dettordini_pkey PRIMARY KEY (id);


--
-- TOC entry 3361 (class 2606 OID 16749)
-- Name: dettpromo dettpromo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettpromo
    ADD CONSTRAINT dettpromo_pkey PRIMARY KEY (id);


--
-- TOC entry 3363 (class 2606 OID 16751)
-- Name: dettscontrini dettscontrini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettscontrini
    ADD CONSTRAINT dettscontrini_pkey PRIMARY KEY (id);


--
-- TOC entry 3365 (class 2606 OID 16753)
-- Name: ecrliv1 ecrliv1_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ecrliv1
    ADD CONSTRAINT ecrliv1_pkey PRIMARY KEY (id);


--
-- TOC entry 3367 (class 2606 OID 16755)
-- Name: ecrliv2 ecrliv2_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ecrliv2
    ADD CONSTRAINT ecrliv2_pkey PRIMARY KEY (id, idliv1);


--
-- TOC entry 3369 (class 2606 OID 16757)
-- Name: famassort famassort_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.famassort
    ADD CONSTRAINT famassort_pkey PRIMARY KEY (id);


--
-- TOC entry 3371 (class 2606 OID 16759)
-- Name: ingredienti ingredienti_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ingredienti
    ADD CONSTRAINT ingredienti_pkey PRIMARY KEY (codart);


--
-- TOC entry 3373 (class 2606 OID 16761)
-- Name: iva iva_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.iva
    ADD CONSTRAINT iva_pkey PRIMARY KEY (idiva);


--
-- TOC entry 3375 (class 2606 OID 16763)
-- Name: listini listini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.listini
    ADD CONSTRAINT listini_pkey PRIMARY KEY (id);


--
-- TOC entry 3377 (class 2606 OID 16765)
-- Name: movimenti movimenti_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimenti
    ADD CONSTRAINT movimenti_pkey PRIMARY KEY (codart);


--
-- TOC entry 3379 (class 2606 OID 16767)
-- Name: ordini ordini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT ordini_pkey PRIMARY KEY (id);


--
-- TOC entry 3381 (class 2606 OID 16769)
-- Name: promo promo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.promo
    ADD CONSTRAINT promo_pkey PRIMARY KEY (idpromo);


--
-- TOC entry 3383 (class 2606 OID 16771)
-- Name: scontrini scontrini_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.scontrini
    ADD CONSTRAINT scontrini_pkey PRIMARY KEY (id);


--
-- TOC entry 3385 (class 2606 OID 16773)
-- Name: tipopromo tipopromo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tipopromo
    ADD CONSTRAINT tipopromo_pkey PRIMARY KEY (idtipopromo);


--
-- TOC entry 3349 (class 1259 OID 16774)
-- Name: idx_cognome; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cognome ON public.clienti USING btree (cognome);


--
-- TOC entry 3340 (class 1259 OID 16775)
-- Name: idx_descrizione; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_descrizione ON public.articoli USING btree (descrizione);


--
-- TOC entry 3395 (class 2620 OID 16776)
-- Name: coupons tr_inserisci_coupon; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_inserisci_coupon BEFORE INSERT ON public.coupons FOR EACH ROW EXECUTE FUNCTION public.tr_inserisci_coupon();


--
-- TOC entry 3396 (class 2620 OID 16777)
-- Name: coupons tr_modifica_coupons; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER tr_modifica_coupons BEFORE UPDATE ON public.coupons FOR EACH ROW EXECUTE FUNCTION public.tr_modifica_coupons();

ALTER TABLE public.coupons DISABLE TRIGGER tr_modifica_coupons;


--
-- TOC entry 3388 (class 2606 OID 16778)
-- Name: barcode articoli_fk_barcode; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barcode
    ADD CONSTRAINT articoli_fk_barcode FOREIGN KEY (codart) REFERENCES public.articoli(codart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3390 (class 2606 OID 16783)
-- Name: classecr articoli_fk_classecr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.classecr
    ADD CONSTRAINT articoli_fk_classecr FOREIGN KEY (codart) REFERENCES public.articoli(codart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3392 (class 2606 OID 16788)
-- Name: dettlistini articoli_fk_dettlistini; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettlistini
    ADD CONSTRAINT articoli_fk_dettlistini FOREIGN KEY (codart) REFERENCES public.articoli(codart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3389 (class 2606 OID 16793)
-- Name: cards clienti_fk_cards; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT clienti_fk_cards FOREIGN KEY (codfidelity) REFERENCES public.clienti(codfidelity);


--
-- TOC entry 3386 (class 2606 OID 16798)
-- Name: articoli famassort_fk_articoli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.articoli
    ADD CONSTRAINT famassort_fk_articoli FOREIGN KEY (idfamass) REFERENCES public.famassort(id) ON UPDATE CASCADE;


--
-- TOC entry 3391 (class 2606 OID 16803)
-- Name: deprifpromo fk_deprifpromo_promo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deprifpromo
    ADD CONSTRAINT fk_deprifpromo_promo FOREIGN KEY (idpromo) REFERENCES public.promo(idpromo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3393 (class 2606 OID 16808)
-- Name: dettlistini fk_dettlistini_listini; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettlistini
    ADD CONSTRAINT fk_dettlistini_listini FOREIGN KEY (idlist) REFERENCES public.listini(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3394 (class 2606 OID 16813)
-- Name: dettpromo fk_dettpromo_promo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dettpromo
    ADD CONSTRAINT fk_dettpromo_promo FOREIGN KEY (idpromo) REFERENCES public.promo(idpromo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3387 (class 2606 OID 16818)
-- Name: articoli iva_fk_articoli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.articoli
    ADD CONSTRAINT iva_fk_articoli FOREIGN KEY (idiva) REFERENCES public.iva(idiva);


-- Completed on 2024-08-07 22:32:04 UTC

--
-- PostgreSQL database dump complete
--

