# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

namespace :doc do
  task :app do
    redirect = File.join('doc', 'redirect.html')
    index = File.join('doc', 'app', 'index.html')
    cp redirect, index, :verbose => true
  end
end
