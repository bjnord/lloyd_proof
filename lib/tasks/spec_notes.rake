# Make sure 'rake notes' finds notes in all our source code directories
#
# HT: http://vgoff.posterous.com/rails-rake-annotations-and-a-solution

class SourceAnnotationExtractor
  alias orig_find find

  def find(dirs=%w(app config lib script test))
    # additional directories we want scanned
    dirs << "spec"
    orig_find(dirs)
  end
end
