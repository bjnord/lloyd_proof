namespace :doc do
  task :app do
    redirect = File.join('doc', 'redirect.html')
    index = File.join('doc', 'app', 'index.html')
    cp redirect, index, :verbose => true
  end
end
