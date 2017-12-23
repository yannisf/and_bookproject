package fraglab.net.bookproject;

enum ResultField {
    ISBN(R.id.value_isbn_id),
    TITLE(R.id.value_title_id),
    AUTHOR(R.id.value_author_id),
    SOURCE_URL(R.id.value_source_url_id),
    PUBLISHER(R.id.value_publisher_id);

    public final int id;

    ResultField(int id) {
        this.id = id;
    }
}
