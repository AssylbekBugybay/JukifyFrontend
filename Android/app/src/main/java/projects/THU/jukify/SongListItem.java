package projects.THU.jukify;

/**
 * Class for Song ListView item
 */
public class SongListItem {
    private String spotifyId;
    private String name;
    private String artist;
    private String album;


    public SongListItem(String id, String name, String author, String album)
    {
        this.spotifyId = id;
        this.name = name;
        this.album = album;
        this.artist = author;
    }

    public String getName() {
        return name;
    }
    public String getAlbum() { return album; }
    public String getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "SongItem{" +
                "name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", author='" + artist + '\'' +
                '}';
    }
}
