(define (domain blocksword)
(:predicates
(sin_nada_encima ?x)
(encima_mesa ?x)
(encima_bloque ?x ?y)
)
(:action apilar
:parameters (?ob ?underob)
:precondition (and (sin_nada_encima ?ob) (sin_nada_encima ?underob) (encima_mesa ?ob))
:effect (and (sin_nada_encima ?ob) (encima_bloque ?ob ?underob) (not (sin_nada_encima ?underob)) (not (encima_mesa ?ob)))
)
(:action apilar_directamente
:parameters (?ob ?underob ?other_ob)
:precondition (and (sin_nada_encima ?ob) (sin_nada_encima ?other_ob) (encima_bloque ?ob ?underob) (not (encima_bloque ?ob ?other_ob)))
:effect (and (sin_nada_encima ?ob) (not (sin_nada_encima ?other_ob)) (sin_nada_encima ?underob) (encima_bloque ?ob ?other_ob))
)
(:action desapilar
:parameters (?ob ?underob)
:precondition (and (encima_bloque ?ob ?underob) (sin_nada_encima ?ob))
:effect (and (sin_nada_encima ?underob) (not (encima_bloque ?ob ?underob)) (encima_mesa ?ob)))
)