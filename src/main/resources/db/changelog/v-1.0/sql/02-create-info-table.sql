CREATE TABLE info (
    id bigserial primary key,
    rules text default null,
    documents text default null,
    transportation text default null,
    house_for_a_puppy text default null,
    home_for_an_adult_animal text default null,
    home_for_an_animal_with_disabilities text default null,
    tips_from_a_dog_handler text default null,
    recommendations_of_a_dog_handler text default null,
    reasons_for_refusal text default null
)
GO