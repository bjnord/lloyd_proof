# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

module ReturnStatus
  class StatusFormatter
    attr_reader :status

    def initialize(sync_id)
      @status = {:sync_id => sync_id, :status => :ok}
    end

    def errors=(errors)
      @status.merge!({:status => :error, :errors => errors})
    end
  end

  # <http://stackoverflow.com/questions/3357712/static-methods-in-ruby-modules>
  def self.included(o)  #:nodoc:
    o.extend(ClassMethods)
  end

  def save_and_return_status(sync_id)
    formatter = StatusFormatter.new(sync_id)
    formatter.errors = self.errors.full_messages unless self.save
    formatter.status
  end

  module ClassMethods
    def create_and_return_status(attr)
      formatter = StatusFormatter.new(attr.delete(:sync_id))
      begin
        corr = Correction.create(attr)
        formatter.errors = corr.errors.full_messages unless corr.persisted?
      rescue ActiveModel::MassAssignmentSecurity::Error => e
        formatter.errors = [e.message]
      end
      formatter.status
    end
  end
end
